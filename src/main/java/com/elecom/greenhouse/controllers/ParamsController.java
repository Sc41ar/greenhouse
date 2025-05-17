package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.model.dto.CultureDataChangeRequest;
import com.elecom.greenhouse.service.CultureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/params")
@Tag(name = "Params Controller", description = "Endpoints to manage params")
public class ParamsController {

    private final CultureService cultureService;

    public ParamsController(CultureService cultureService) {
        this.cultureService = cultureService;
    }

    @GetMapping
    @Operation(summary = "Get all params")
    public void getParams() {
    }

    @PostMapping
    @Operation(summary = "Update params")
    public CultureDataChangeRequest updateParams(
            @RequestBody @Valid CultureDataChangeRequest request) {
        return cultureService.changeCultureData(request);
    }

}
