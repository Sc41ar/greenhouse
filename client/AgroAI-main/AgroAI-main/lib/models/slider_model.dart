import 'package:flutter/material.dart';

class SliderModel extends ChangeNotifier {
  // 🔒 Приватные управляющие параметры (от сценариев)
  double _lightExposure = 0;
  double _lightExposurePause = 0;
  double _watering = 0;
  double _wateringPause = 0;
  double _wateringSeconds = 0;

  // 🔒 Приватные сенсорные данные
  double _temperature = 0;
  double _temperatureWater = 0;
  double _humidityShare = 0;
  double _soilMoisture = 0;
  double _co2 = 0;

  int _wateringFrequency = 0;

  // 🌐 Подключение
  String deviceIp = '81.31.244.60';
  int devicePort = 8099;

  int plantId = 0;

  // 📝 Информация о растении
  String plantName = '';
  String article = '';
  String soilType = '';
  String fertilizationSchedule = '';
  List<String> wateringSchedule = [];

  // 📥 Обновление IP и порта
  void updateConnection(String ip, int port) {
    deviceIp = ip;
    devicePort = port;
    notifyListeners();
  }

  // 📥 Обновление сенсорных данных (влажность, CO2 и т.п.)
  void updateSensorData(Map<String, dynamic> data) {
    temperature = (data['temperature'] ?? 0).toDouble();
    temperatureWater = (data['waterTemperature'] ?? 0).toDouble();
    humidityShare = (data['humidity'] ?? 0).toDouble() * 100;

    soilMoisture = (data['soilMoisture'] ?? 0).toDouble() * 100;
    co2 = (data['co2'] ?? 0).toDouble();
  }

  // 📥 Обновление управляющих параметров растения
  void updatePlantParams(Map<String, dynamic> data) {
    plantId = data['plantId'] ?? 0;
    // watering = (data['watering'] ?? 0).toDouble();
    wateringPause = (data['wateringPause'] ?? 0).toDouble();
    wateringSeconds = (data['wateringSeconds'] ?? 0).toDouble();
    lightExposure = (data['lightExposure'] ?? 0).toDouble();
    lightExposurePause = (data['lightExposurePause'] ?? 0).toDouble();
    notifyListeners();
  }

  // 📥 Обновление из модели
  void applyJson(Map<String, dynamic> json) {
    // temperature = (json['temperature'] ?? _temperature).toDouble();
    // temperatureWater =
    //     (json['temperatureWater'] ?? _temperatureWater).toDouble();
    // humidityShare = (json['humidityShare'] ?? _humidityShare).toDouble();
    lightExposure = (json['lightExposure'] ?? _lightExposure).toDouble();
    lightExposurePause =
        (json['lightExposurePause'] ?? _lightExposurePause).toDouble();

    wateringSeconds = (json['wateringSeconds'] ?? _wateringSeconds).toDouble();
    wateringPause = (json['wateringPause'] ?? wateringPause).toDouble();

    plantName = json['plantName'] ?? plantName;
    article = json['article'] ?? article;
    soilType = json['soilType'] ?? soilType;
    fertilizationSchedule =
        json['fertilizationSchedule'] ?? fertilizationSchedule;
    wateringSchedule =
        List<String>.from(json['wateringSchedule'] ?? wateringSchedule);

    notifyListeners();
  }

  // 🔓 Геттеры управляющих параметров
  double get lightExposure => _lightExposure;
  double get lightExposurePause => _lightExposurePause;
  double get watering => _watering;
  double get wateringPause => _wateringPause;
  double get wateringSeconds => _wateringSeconds;

  // 🔓 Геттеры сенсоров
  double get temperature => _temperature;
  double get temperatureWater => _temperatureWater;
  double get humidityShare => _humidityShare;
  double get soilMoisture => _soilMoisture;
  double get co2 => _co2;

  int get wateringFrequency => _wateringFrequency;

  // 🛠️ Сеттеры управляющих параметров
  set lightExposure(double value) {
    _lightExposure = value;
    notifyListeners();
  }

  set lightExposurePause(double value) {
    _lightExposurePause = value;
    notifyListeners();
  }

  set watering(double value) {
    _watering = value;
    notifyListeners();
  }

  set wateringPause(double value) {
    _wateringPause = value;
    notifyListeners();
  }

  set wateringSeconds(double value) {
    _wateringSeconds = value;
    notifyListeners();
  }

  // 🛠️ Сеттеры сенсоров
  set temperature(double value) {
    _temperature = value;
    notifyListeners();
  }

  set temperatureWater(double value) {
    _temperatureWater = value;
    notifyListeners();
  }

  set humidityShare(double value) {
    _humidityShare = value;
    notifyListeners();
  }

  set soilMoisture(double value) {
    _soilMoisture = value;
    notifyListeners();
  }

  set co2(double value) {
    _co2 = value;
    notifyListeners();
  }

  set wateringFrequency(int value) {
    _wateringFrequency = value;
    notifyListeners();
  }

  List<Map<String, dynamic>> chatMessages = [];

  void addMessage(Map<String, dynamic> message) {
    chatMessages.add(message);
    notifyListeners();
  }

  void clearMessages() {
    chatMessages.clear();
    notifyListeners();
  }
}
