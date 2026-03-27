package com.example.kombatgame.controller;

import com.example.kombatgame.dto.StrategyValidationDTO;
import com.example.kombatgame.service.StrategyValidationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strategy")
public class StrategyController {

    private final StrategyValidationService validationService;

    public StrategyController(StrategyValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    public StrategyValidationDTO validate(@RequestBody ValidateStrategyRequest req) {
        System.out.println("[DEBUG] StrategyController.validate() called with strategy length: " +
                (req.strategy != null ? req.strategy.length() : "null"));
        System.out.println("[DEBUG] Strategy preview: " +
                (req.strategy != null ? req.strategy.substring(0, Math.min(100, req.strategy.length())) : "null"));

        StrategyValidationService.ValidationResult result = validationService.validate(req.strategy);
        System.out.println("[DEBUG] Validation result: valid=" + result.isValid() + ", message=" + result.getMessage());

        return new StrategyValidationDTO(result.isValid(), result.getMessage());
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("[DEBUG] StrategyController.test() called");
        return "Strategy endpoint working!";
    }

    public static class ValidateStrategyRequest {
        public String strategy;
    }
}
