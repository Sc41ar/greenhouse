package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.model.dto.ModelResponse;
import com.elecom.greenhouse.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/model")
@Tag(name = "Model Controller")
@RequiredArgsConstructor
public class ModelController {

    private final PromptService promptService;

    @GetMapping("/article")
    @Operation(description = """
            Получение статьи о растении по названию.
            Принимает на вход название растения и возвращает ответ от модели.
            """
    )
    public ModelResponse getArticle(@RequestParam("culture") String culture) {
        return promptService.sendPrompt("Как вырастить " + culture + "?");
    }

    @PostMapping("/article")
    @Operation(description = """
             Парсинг ответа от модели. Принимает на вход строку с ответом от модели возвращает объект с целевой информацией о растении.\s
            \s""")
    public ResponseEntity<ModelResponse> getArticleFromString(
//            @RequestParam("culture") String culture
    ) throws Exception {
        String culture = """
                {"id":"a9956eef-e163-4ed3-a0c4-5d3e80a4af36","object":"chat.completion","created":1747419254,"model":"deepseek-chat","choices":[{"index":0,"message":{"role":"assistant","content":"```json\\n{\\n    \\"plantName\\": \\"Mint\\",\\n    \\"article\\": \\"Мята — это неприхотливое растение, которое хорошо растет в вертикальных теплицах для коммерческого выращивания. Она предпочитает влажную почву, но не терпит застоя воды. Мята быстро разрастается, поэтому важно контролировать ее рост. Для максимального урожая рекомендуется регулярная обрезка и подкормка.\\",\\n    \\"humidityShare\\": 0.7,\\n    \\"temperature\\": 18,\\n    \\"wateringFrequency\\": 2,\\n    \\"wateringSchedule\\": [\\"08:00\\", \\"16:00\\"],\\n    \\"soilType\\": \\"Хорошо дренированная, плодородная почва с pH 6.0–7.0\\",\\n    \\"lightExposure\\": 60, \\"lightExposurePause\\":95,\\n    \\"fertilizationSchedule\\": \\"Раз в 2–3 недели сбалансированным удобрением\\"\\n}\\n```"},"logprobs":null,"finish_reason":"stop"}],"usage":{"prompt_tokens":206,"completion_tokens":229,"total_tokens":435,"prompt_tokens_details":{"cached_tokens":0},"prompt_cache_hit_tokens":0,"prompt_cache_miss_tokens":206},"system_fingerprint":"fp_8802369eaa_prod0425fp8"}\\n{\\n    \\"plantName\\": \\"Mint\\",\\n    \\"article\\": \\"Мята — это неприхотливое растение, которое хорошо растет в вертикальных теплицах для коммерческого выращивания. Она предпочитает влажную почву, но не терпит застоя воды. Мята быстро разрастается, поэтому важно контролировать ее рост. Для максимального урожая рекомендуется регулярная обрезка и подкормка.\\",\\n    \\"humidityShare\\": 0.7,\\n    \\"temperature\\": 18,\\n    \\"wateringFrequency\\": 2,\\n    \\"wateringSchedule\\": [\\"08:00\\", \\"16:00\\"],\\n    \\"soilType\\": \\"Хорошо дренированная, плодородная почва с pH 6.0–7.0\\",\\n    \\"lightExposure\\": 6,\\n    \\"fertilizationSchedule\\": \\"Раз в 2–3 недели сбалансированным удобрением\\"\\n}\\n```"},"logprobs":null,"finish_reason":"stop"}],"usage":{"prompt_tokens":206,"completion_tokens":229,"total_tokens":435,"prompt_tokens_details":{"cached_tokens":0},"prompt_cache_hit_tokens":0,"prompt_cache_miss_tokens":206},"system_fingerprint":"fp_8802369eaa_prod0425fp8"}
                """;
        ModelResponse modelResponse = new ModelResponse();
        modelResponse = promptService.processResponse(culture);
        promptService.saveCultureData(modelResponse);
        return ResponseEntity.ok(modelResponse);
    }
}
