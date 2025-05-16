package com.elecom.greenhouse.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/params")
@Tag(name = "Params Controller", description = "Endpoints to manage params")
public class ParamsController {

    @GetMapping
    @Operation(summary = "Get all params")
    public void getParams() {
    }

}
