import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import '../widgets/message_bubble.dart';
import '../models/slider_model.dart';

class ChatPage extends StatefulWidget {
  final Function(Map<String, dynamic>) onApplyJson;

  const ChatPage({
    super.key,
    required this.onApplyJson,
  });

  @override
  State<ChatPage> createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  final TextEditingController _controller = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _isWaiting = false;

  void _sendMessage() async {
    final text = _controller.text.trim();
    if (text.isEmpty) return;

    final model = context.read<SliderModel>();
    model.addMessage({'text': text, 'isMe': true});
    setState(() => _isWaiting = true);

    _controller.clear();
    _scrollToBottom();

    final result = await _generateBotReply(text);
    model.addMessage(result);
    setState(() => _isWaiting = false);

    _scrollToBottom();
  }

  void _scrollToBottom() {
    Future.delayed(const Duration(milliseconds: 100), () {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    });
  }

  Future<Map<String, dynamic>> _generateBotReply(String userMessage) async {
    try {
      final model = Provider.of<SliderModel>(context, listen: false);
      final ip = model.deviceIp;
      final port = model.devicePort;

      final uri =
          Uri.parse('http://$ip:$port/model/article?culture=$userMessage');
      final response = await http.get(uri);

      if (response.statusCode == 200) {
        final decoded = utf8.decode(response.bodyBytes);
        final data = json.decode(decoded);

        if (data is Map<String, dynamic> && data.containsKey('article')) {
          return {
            'text': data['article'].toString(),
            'isMe': false,
            'json': data,
          };
        }
      }
    } catch (e) {
      return {'text': 'Ошибка при запросе: $e', 'isMe': false};
    }

    return {'text': 'Бот: Я получил "$userMessage"', 'isMe': false};
  }

  void _clearChat() {
    context.read<SliderModel>().clearMessages();
  }

  @override
  Widget build(BuildContext context) {
    final model = context.watch<SliderModel>();
    final messages = model.chatMessages;

    return Scaffold(
      appBar: AppBar(
        title: const Text('DeepSeek Агроном'),
        actions: [
          PopupMenuButton<String>(
            onSelected: (value) {
              if (value == 'clear') _clearChat();
            },
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'clear', child: Text('Очистить чат')),
            ],
          )
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(8),
              itemCount: messages.length + (_isWaiting ? 1 : 0),
              itemBuilder: (context, index) {
                if (_isWaiting && index == messages.length) {
                  return const Padding(
                    padding: EdgeInsets.symmetric(vertical: 10.0),
                    child: Row(
                      children: [
                        SizedBox(
                          width: 24,
                          height: 24,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        ),
                        SizedBox(width: 12),
                        Text('DeepSeek печатает...',
                            style: TextStyle(color: Colors.white70))
                      ],
                    ),
                  );
                }

                final msg = messages[index];
                return MessageBubble(
                  message: msg['text'],
                  isMe: msg['isMe'],
                  jsonData: msg['json'],
                  onApplyJson: msg['json'] != null ? widget.onApplyJson : null,
                );
              },
            ),
          ),
          _buildInput(),
        ],
      ),
    );
  }

  Widget _buildInput() {
    return Padding(
      padding: const EdgeInsets.all(8),
      child: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: Container(
                  height: 60,
                  decoration: BoxDecoration(
                    color: Colors.grey[850],
                    borderRadius: const BorderRadius.only(
                      bottomLeft: Radius.circular(12),
                      topRight: Radius.circular(12),
                    ),
                  ),
                  padding: const EdgeInsets.symmetric(horizontal: 12),
                  child: TextField(
                    controller: _controller,
                    onSubmitted: (_) => _sendMessage(),
                    decoration: const InputDecoration(
                      hintText: 'Введите сообщение...',
                      border: InputBorder.none,
                    ),
                    style: const TextStyle(color: Colors.white),
                  ),
                ),
              ),
              const SizedBox(width: 10),
              Container(
                decoration: const BoxDecoration(
                  shape: BoxShape.circle,
                  color: Colors.deepOrange,
                ),
                child: IconButton(
                  icon: const Icon(Icons.send, color: Colors.white),
                  onPressed: _sendMessage,
                ),
              ),
            ],
          ),
          const SizedBox(
            height: 20,
          )
        ],
      ),
    );
  }
}
