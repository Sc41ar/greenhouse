#include "DHT.h"
#include <WiFi.h>
#include <WebServer.h>
#include <OneWire.h>

#define soil_mois 32
#define air_mois 27
#define carbon_mono 34
#define water_pomp 13
#define uf_light 12
bool water_status = LOW;
bool light_status = LOW;

const char* ssid = "POCO_M5";
const char* password = "13579024680";
WebServer server(80);
OneWire ds(14);
DHT dht(air_mois, DHT11);

void setup() {
  pinMode(soil_mois, INPUT);
  pinMode(carbon_mono, INPUT);
  pinMode(water_pomp, OUTPUT);
  pinMode(uf_light, OUTPUT);
  dht.begin();
  Serial.begin(115200);

  delay(100);
  Serial.println("Connecting to ");
  Serial.println(ssid);
  //connect to your local wi-fi network
  WiFi.begin(ssid, password);
  //check wi-fi is connected to wi-fi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected..!");
  pinMode(2, OUTPUT);
  digitalWrite(2, 1);
  delay(100);
  digitalWrite(2, 0);
  delay(100);
  digitalWrite(2, 1);
  delay(100);
  digitalWrite(2, 0);
  Serial.print("Got IP: ");  Serial.println(WiFi.localIP());
  server.on("/", handle_OnConnect);
  server.on("/water_on", handle_water_on);
  server.on("/water_off", handle_water_off);
  server.on("/light_on", handle_light_on);
  server.on("/light_off", handle_light_off);
  server.on("/get_data", get_data);
  server.onNotFound(handle_NotFound);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  /*Serial.print(analogRead(soil_mois));
    Serial.print("\t");
    Serial.print(dht.readTemperature());
    Serial.print("\t");
    Serial.print(dht.readHumidity());
    Serial.print("\t");
    Serial.println(analogRead(carbon_mono));
    delay(200);*/
  /*if (Serial.available() > 0) {  //если есть доступные данные
    // считываем байт
    int incomingByte = Serial.read() - 48;
    if (incomingByte != -38) {
      switch (incomingByte) {
        case 0:
          digitalWrite(water_pomp, 1);
          Serial.println("Water_pomp - High");
          break;
        case 1:
          digitalWrite(uf_light, 1);
          Serial.println("Uf_light - High");
          break;
        case 3:
          digitalWrite(water_pomp, 0);
          Serial.println("Water_pomp - Low");
          break;
        case 4:
          digitalWrite(uf_light, 0);
          Serial.println("Uf_light - Low");
          break;
        default:
          break;
      }
    }
    }*/

  server.handleClient();
  if (water_status)
    digitalWrite(water_pomp, HIGH);
  else
    digitalWrite(water_pomp, LOW);
  if (light_status)
    digitalWrite(uf_light, HIGH);
  else
    digitalWrite(uf_light, LOW);
}

void handle_OnConnect() {
  water_status = LOW;
  light_status = LOW;
  Serial.println("Water Status: OFF | Light Status: OFF");
  server.send(200, "text/html", SendHTML(water_status, light_status));
}
void handle_water_on() {
  water_status = HIGH;
  Serial.println("Water Status: ON");
  server.send(200, "text/html", SendHTML(true, light_status));
}
void handle_water_off() {
  water_status = LOW;
  Serial.println("Water Status: OFF");
  server.send(200, "text/html", SendHTML(false, light_status));
}
void handle_light_on() {
  light_status = HIGH;
  Serial.println("Light Status: ON");
  server.send(200, "text/html", SendHTML(water_status, true));
}
void handle_light_off() {
  light_status = LOW;
  Serial.println("Light Status: OFF");
  server.send(200, "text/html", SendHTML(water_status, false));
}
void handle_NotFound() {
  server.send(404, "text/plain", "Not found");
}
void get_data() {
  Serial.println("Data output");
  server.send(200, "text/html", getDataHTLM());
}
String getDataHTLM() {
  String ptr = String(map(analogRead(soil_mois), 1500, 4095, 100.0, 0.0) / 100.0); // 0.775
  ptr += ";";
  ptr += String(dht.readTemperature());
  ptr += ";";
  ptr += String(dht.readHumidity() / 100.0); // 75.00 -> 0.75
  ptr += ";";
  ptr += String(analogRead(carbon_mono));
  ptr += ";";

  // Определяем температуру от датчика DS18b20
  byte data[2]; // Место для значения температуры
  ds.reset(); // Начинаем взаимодействие со сброса всех предыдущих команд и параметров
  ds.write(0xCC); // Даем датчику DS18b20 команду пропустить поиск по адресу. В нашем случае только одно устрйоство
  ds.write(0x44); // Даем датчику DS18b20 команду измерить температуру. Само значение температуры мы еще не получаем - датчик его положит во внутреннюю память
  delay(1000); // Микросхема измеряет температуру, а мы ждем.
  ds.reset(); // Теперь готовимся получить значение измеренной температуры
  ds.write(0xCC);
  ds.write(0xBE); // Просим передать нам значение регистров со значением температуры
  // Получаем и считываем ответ
  data[0] = ds.read(); // Читаем младший байт значения температуры
  data[1] = ds.read(); // А теперь старший
  // Формируем итоговое значение:
  //    - сперва "склеиваем" значение,
  //    - затем умножаем его на коэффициент, соответсвующий разрешающей способности (для 12 бит по умолчанию - это 0,0625)
  float temperature =  ((data[1] << 8) | data[0]) * 0.0625;
  // Выводим полученное значение температуры в монитор порта
  // Serial.println(temperature);
  ptr += String(temperature);

  return ptr;
}

String SendHTML(uint8_t water_stat, uint8_t light_stat) {
  String ptr = "<!DOCTYPE html> <html>\n";
  ptr += "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n";
  ptr += "<title>Управление светодиодом</title>\n";
  ptr += "<style>html { font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}\n";
  ptr += "body{margin-top: 50px;} h1 {color: #444444;margin: 50px auto 30px;} h3 {color: #444444;margin-bottom: 50px;}\n";
  ptr += ".button {display: block;width: 80px;background-color: #3498db;border: none;color: white;padding: 13px 30px;text-decoration: none;font-size: 25px;margin: 0px auto 35px;cursor: pointer;border-radius: 4px;}\n";
  ptr += ".button-on {background-color: #3498db;}\n";
  ptr += ".button-on:active {background-color: #2980b9;}\n";
  ptr += ".button-off {background-color: #34495e;}\n";
  ptr += ".button-off:active {background-color: #2c3e50;}\n";
  ptr += "p {font-size: 14px;color: #888;margin-bottom: 10px;}\n";
  ptr += "</style>\n";
  ptr += "</head>\n";
  ptr += "<body>\n";
  ptr += "<h1>ESP32 Веб сервер</h1>\n";
  ptr += "<h3>Режим станции (STA)</h3>\n";
  if (water_stat)
  {
    ptr += "<p>Состояние Water: ON.</p><a class=\"button button-off\" href=\"/water_off\">ВЫКЛ.</a>\n";
  }
  else
  {
    ptr += "<p>Состояние Water: OFF.</p><a class=\"button button-on\" href=\"/water_on\">ВКЛ.</a>\n";
  }
  if (light_stat)
  {
    ptr += "<p>Состояние Light: ON.</p><a class=\"button button-off\" href=\"/light_off\">ВЫКЛ.</a>\n";
  }
  else
  {
    ptr += "<p>Состояние Light: OFF.</p><a class=\"button button-on\" href=\"/light_on\">ВКЛ.</a>\n";
  }
  ptr += "</body>\n";
  ptr += "</html>\n";
  return ptr;
}
