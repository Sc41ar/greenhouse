import 'package:flutter/material.dart';

class AppTheme {
  static final lightTheme = ThemeData(
    brightness: Brightness.dark,
    scaffoldBackgroundColor: Colors.black,
    primarySwatch: Colors.deepOrange,
    appBarTheme: const AppBarTheme(
      backgroundColor: Color.fromARGB(255, 244, 94, 49),
      foregroundColor: Colors.white,
    ),
    sliderTheme: const SliderThemeData(
      activeTrackColor: Colors.deepOrange,
      thumbColor: Colors.deepOrange,
      inactiveTrackColor: Colors.white24,
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.deepOrange,
        foregroundColor: Colors.white,
      ),
    ),
  );

  static final darkTheme = lightTheme;
}
