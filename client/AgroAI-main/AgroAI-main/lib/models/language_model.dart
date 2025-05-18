import 'package:flutter/material.dart';

class LanguageModel extends ChangeNotifier {
  String _language = 'ru';

  String get language => _language;

  void setLanguage(String langCode) {
    _language = langCode;
    notifyListeners();
  }
}
