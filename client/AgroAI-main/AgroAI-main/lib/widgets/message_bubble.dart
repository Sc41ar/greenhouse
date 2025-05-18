import 'package:flutter/material.dart';

class MessageBubble extends StatelessWidget {
  final String message;
  final bool isMe;
  final Map<String, dynamic>? jsonData;
  final Function(Map<String, dynamic>)? onApplyJson;

  const MessageBubble({
    super.key,
    required this.message,
    required this.isMe,
    this.jsonData,
    this.onApplyJson,
  });

  @override
  Widget build(BuildContext context) {
    final color = isMe ? Colors.deepOrange : Colors.grey[800];
    final alignment = isMe ? Alignment.centerRight : Alignment.centerLeft;

    return Align(
      alignment: alignment,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 8),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: color,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              message,
              style: const TextStyle(color: Colors.white),
            ),
            if (!isMe && jsonData != null && onApplyJson != null)
              Padding(
                padding: const EdgeInsets.only(top: 12),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.deepOrange,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(24),
                    ),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                  ),
                  onPressed: () {
                    FocusScope.of(context).unfocus(); // ⬅️ закрытие клавиатуры
                    onApplyJson!(jsonData!);
                  },
                  child: const Text('Принять параметры'),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
