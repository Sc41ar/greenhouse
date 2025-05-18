import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'pages/chat_page.dart';
import 'pages/control_page.dart';
import 'theme/app_theme.dart';
import 'models/slider_model.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => SliderModel(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AgroAI',
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.dark,
      home: const MainScreen(),
    );
  }
}

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  final PageController _pageController = PageController();

  void applyJson(Map<String, dynamic> json) {
    // Обновляем модель
    Provider.of<SliderModel>(context, listen: false).applyJson(json);

    // Переключаемся на ControlPage
    _pageController.animateToPage(
      0,
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageView(
        controller: _pageController,
        // свайп ВКЛЮЧЕН
        children: [
          const ControlPage(),
          ChatPage(onApplyJson: applyJson),
        ],
      ),
    );
  }
}
