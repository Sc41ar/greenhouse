import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import '../models/slider_model.dart';

class ControlPage extends StatefulWidget {
  const ControlPage({super.key});

  @override
  State<ControlPage> createState() => _ControlPageState();
}

class _ControlPageState extends State<ControlPage> {
  final TextEditingController _ipController = TextEditingController();
  final TextEditingController _portController = TextEditingController();

  String? _ipError;
  String? _portError;
  bool _editingAddress = false;
  bool _isConnected = false;

  final _ipRegex =
      RegExp(r'^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\.|$)){4}$');

  Timer? _timer;

  @override
  void initState() {
    super.initState();

    final model = context.read<SliderModel>();
    _ipController.text = model.deviceIp;
    _portController.text = model.devicePort.toString();

    _fetchSensorData(); // первая загрузка
    _timer =
        Timer.periodic(const Duration(seconds: 10), (_) => _fetchSensorData());
  }

  @override
  void dispose() {
    _timer?.cancel();
    _ipController.dispose();
    _portController.dispose();
    super.dispose();
  }

  void _fetchSensorData() async {
    final model = context.read<SliderModel>();
    final ip = model.deviceIp;
    final port = model.devicePort;

    final url = Uri.parse('http://$ip:$port/sensors/data');

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        if (data is Map<String, dynamic>) {
          model.updateSensorData(data);
          if (!mounted) return; // ✅ добавлено
          setState(() => _isConnected = true);
        }
      } else {
        if (!mounted) return; // ✅ добавлено
        setState(() => _isConnected = false);
        debugPrint('Ошибка сервера: ${response.statusCode}');
      }
    } catch (e) {
      if (!mounted) return; // ✅ добавлено
      setState(() => _isConnected = false);
      debugPrint('Ошибка подключения: $e');
      debugPrint('IP: $ip');
      debugPrint('PORT: $port');
    }
  }

  void _startEdit() {
    final model = context.read<SliderModel>();
    _ipController.text = model.deviceIp;
    _portController.text = model.devicePort.toString();
    setState(() {
      _editingAddress = true;
      _ipError = null;
      _portError = null;
    });
  }

  void _cancelEdit() {
    setState(() {
      _editingAddress = false;
      _ipError = null;
      _portError = null;
    });
  }

  void _applyEdit() {
    final ip = _ipController.text.trim();
    final port = int.tryParse(_portController.text.trim());

    bool valid = true;

    if (!_ipRegex.hasMatch(ip)) {
      _ipError = 'Неверный IP. Пример: 192.168.1.1';
      valid = false;
    } else {
      _ipError = null;
    }

    if (port == null || port <= 0 || port > 65535) {
      _portError = 'Порт должен быть от 1 до 65535';
      valid = false;
    } else {
      _portError = null;
    }

    if (valid) {
      context.read<SliderModel>().updateConnection(ip, port!);
      setState(() => _editingAddress = false);

      // ✅ Показываем уведомление
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('✅ IP и порт изменены на $ip:$port'),
          duration: const Duration(seconds: 3),
          behavior: SnackBarBehavior.floating,
        ),
      );
    } else {
      setState(() {});
    }
  }

  Future<void> _sendCommandStart(String command) async {
    final model = context.read<SliderModel>();
    final url = Uri.parse(
        'http://${model.deviceIp}:${model.devicePort}/schedule/resume');

    try {
      final response = await http.post(url);
      final success = response.statusCode == 200;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success
              ? '✅ Команда "$command" выполнена'
              : '❌ Ошибка выполнения "$command"'),
          backgroundColor: success ? Colors.green : Colors.red,
          duration: const Duration(seconds: 2),
        ),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('❌ Ошибка подключения: $e'),
          backgroundColor: Colors.red,
          duration: const Duration(seconds: 2),
        ),
      );
    }
  }

  Future<void> _sendCommandStop(String command) async {
    final model = context.read<SliderModel>();
    final url = Uri.parse(
        'http://${model.deviceIp}:${model.devicePort}/schedule/cancel');

    try {
      final response = await http.post(url);
      final success = response.statusCode == 200;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success
              ? '✅ Команда "$command" выполнена'
              : '❌ Ошибка выполнения "$command"'),
          backgroundColor: success ? Colors.green : Colors.red,
          duration: const Duration(seconds: 2),
        ),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('❌ Ошибка подключения: $e'),
          backgroundColor: Colors.red,
          duration: const Duration(seconds: 2),
        ),
      );
    }
  }

  Future<void> _fetchPlantParams(int plantId) async {
    final model = context.read<SliderModel>();
    final ip = model.deviceIp;
    final port = model.devicePort;

    final url = Uri.parse('http://$ip:$port$plantId');

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        if (data is Map<String, dynamic>) {
          model.updatePlantParams(data);
        }
      } else {
        debugPrint('Ошибка параметров: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('Ошибка загрузки параметров: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    final sliders = context.watch<SliderModel>();

    return Scaffold(
      appBar: AppBar(title: const Text('Управление')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            buildSensorData('IP', sliders.deviceIp),
            buildSensorData('Port', sliders.devicePort.toString()),
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    _isConnected ? Icons.cloud_done : Icons.cloud_off,
                    color: _isConnected ? Colors.green : Colors.red,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    _isConnected ? 'Сервер подключен' : 'Нет подключения',
                    style: TextStyle(
                      color: _isConnected ? Colors.green : Colors.red,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 8),
            if (!_editingAddress)
              ElevatedButton(
                onPressed: _startEdit,
                child: const Text('Сменить адрес'),
              ),
            if (_editingAddress) ...[
              const SizedBox(height: 12),
              TextField(
                controller: _ipController,
                decoration: InputDecoration(
                  labelText: 'IP-адрес',
                  hintText: '192.168.1.1',
                  errorText: _ipError,
                  border: const OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _portController,
                decoration: InputDecoration(
                  labelText: 'Порт',
                  hintText: '8099',
                  errorText: _portError,
                  border: const OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: _applyEdit,
                      icon: const Icon(Icons.check),
                      label: const Text('Применить'),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: _cancelEdit,
                      icon: const Icon(Icons.close),
                      label: const Text('Отмена'),
                    ),
                  ),
                ],
              ),
            ],
            const SizedBox(height: 24),
            buildSensorData('Температура воздуха',
                '${sliders.temperature.toStringAsFixed(1)} °C'),
            buildSensorData('Температура воды',
                '${sliders.temperatureWater.toStringAsFixed(1)} °C'),
            buildSensorData('Влажность воздуха',
                '${(sliders.humidityShare).toStringAsFixed(0)} %'),
            buildSensorData('Влажность почвы',
                '${(sliders.soilMoisture).toStringAsFixed(0)} %'),
            buildSensorData('CO2', '${sliders.co2.toStringAsFixed(2)} ppm'),
            const SizedBox(height: 24),
            buildControlBlock(
              title: 'ПОЛИВ',
              sliders: [
                buildSliderControl(
                  context,
                  label: 'Длительность',
                  value: sliders.wateringSeconds,
                  min: 0,
                  max: 120,
                  onChanged: (v) => sliders.wateringSeconds = v,
                ),
                buildSliderControl(
                  context,
                  label: 'Период действия',
                  value: sliders.wateringPause,
                  min: 0,
                  max: 120,
                  onChanged: (v) => sliders.wateringPause = v,
                ),
              ],
            ),
            const SizedBox(height: 16),
            buildControlBlock(
              title: 'УФ СВЕТ',
              sliders: [
                buildSliderControl(
                  context,
                  label: 'Длительность',
                  value: sliders.lightExposure,
                  min: 0,
                  max: 120,
                  onChanged: (v) => sliders.lightExposure = v,
                ),
                buildSliderControl(
                  context,
                  label: 'Период действия',
                  value: sliders.lightExposurePause,
                  min: 0,
                  max: 120,
                  onChanged: (v) => sliders.lightExposurePause = v,
                ),
              ],
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton.icon(
                  onPressed: () => _sendCommandStart('start'),
                  icon: const Icon(Icons.play_arrow),
                  label: const Text('Старт'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                  ),
                ),
                ElevatedButton.icon(
                  onPressed: () => _sendCommandStop('stop'),
                  icon: const Icon(Icons.stop),
                  label: const Text('Стоп'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget buildSensorData(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(fontSize: 16)),
          Text(value,
              style:
                  const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Widget buildControlBlock({
    required String title,
    required List<Widget> sliders,
  }) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(title,
              style:
                  const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          ...sliders,
        ],
      ),
    );
  }

  Widget buildSliderControl(
    BuildContext context, {
    required String label,
    required double value,
    required double min,
    required double max,
    required ValueChanged<double> onChanged,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontSize: 14)),
          Row(
            children: [
              Expanded(
                child: Slider(
                  value: value,
                  min: min,
                  max: max,
                  divisions: 100000,
                  label: value.toStringAsFixed(1),
                  onChanged: onChanged,
                ),
              ),
              SizedBox(
                width: 50,
                child: Text(
                  value.toStringAsFixed(0),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
