import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/language_model.dart';

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageModel>(context);

    return Scaffold(
      appBar:
          AppBar(title: Text(lang.language == 'ru' ? 'Настройки' : 'Settings')),
      body: Column(
        children: [
          ListTile(
            title: const Text('Русский'),
            leading: Radio<String>(
              value: 'ru',
              groupValue: lang.language,
              onChanged: (val) => lang.setLanguage(val!),
            ),
          ),
          ListTile(
            title: const Text('English'),
            leading: Radio<String>(
              value: 'en',
              groupValue: lang.language,
              onChanged: (val) => lang.setLanguage(val!),
            ),
          ),
        ],
      ),
    );
  }
}
