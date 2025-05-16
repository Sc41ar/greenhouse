package com.elecom.greenhouse.util;

import com.elecom.greenhouse.model.dto.ChatCompletionResponse;
import com.elecom.greenhouse.model.dto.ModelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ModelResponse mapToModelResponse(ChatCompletionResponse apiResponse) throws
            Exception {
        // Извлекаем JSON-строку из content
        String content = apiResponse.getChoices().get(0).getMessage().getContent();

        // Удаляем Markdown-обертку (```json ... ```)
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();

        // Преобразуем JSON-строку в объект ModelResponse
        return objectMapper.readValue(content, ModelResponse.class);
    }
}
