package com.elecom.greenhouse.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponse {
    private String id;

    private String object;

    private Long created;

    private String model;

    private List<Choice> choices;

    private Usage usage;

    @JsonProperty("system_fingerprint")
    private String systemFingerPrint;
}
