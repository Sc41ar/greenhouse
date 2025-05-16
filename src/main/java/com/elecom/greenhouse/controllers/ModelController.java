package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.model.dto.ModelResponse;
import com.elecom.greenhouse.service.PromptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/model")
@Tag(name = "Model Controller")
@RequiredArgsConstructor
public class ModelController {

    private final PromptService promptService;

    @GetMapping("/article")
    public String getArticle(@RequestParam("culture") String culture) {
        return promptService.sendPrompt("How to grow " + culture);
    }

    @PostMapping("/article")
    public ResponseEntity<ModelResponse> getArticleFromString(
            @RequestParam("culture") String culture) throws Exception {
        return ResponseEntity.ok(promptService.processResponse(culture));
    }
}
