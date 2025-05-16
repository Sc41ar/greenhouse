package com.elecom.greenhouse.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Usage {
    @JsonProperty("prompt_tokens")
    private int promptTokens;
    @JsonProperty("completion_tokens")
    private int completionTokens;
    @JsonProperty("total_tokens")
    private int totalTokens;
    @JsonProperty("prompt_tokens_details")
    private PromptTokensDetails promptTokensDetails;
    @JsonProperty("prompt_cache_hit_tokens")
    private int promptCacheHitTokens;
    @JsonProperty("prompt_cache_miss_tokens")
    private int promptCacheMissTokens;
}
