package com.elecom.greenhouse.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromptTokensDetails {
    @JsonProperty("cached_tokens")
    private int cachedTokens;
}
