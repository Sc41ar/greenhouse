package com.elecom.greenhouse.controllers;

import com.elecom.greenhouse.model.dto.CultureDataChangeRequest;
import com.elecom.greenhouse.service.CultureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/params")
@Tag(name = "Params Controller", description = "Endpoints to manage params")
public class ParamsController {

    private final CultureService cultureService;

    public ParamsController(CultureService cultureService) {
        this.cultureService = cultureService;
    }

    @GetMapping("/{plantId}")
    @Operation(summary = "Get all params")
    public CultureDataChangeRequest getParams(
            @PathVariable("plantId") long plantId
    ) {
        return cultureService.getCultureData(plantId);
    }

    @PostMapping
    @Operation(summary = "Update params")
    public CultureDataChangeRequest updateParams(
            @RequestBody @Valid CultureDataChangeRequest request) {
        return cultureService.changeCultureData(request);
    }

}
