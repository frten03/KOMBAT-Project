package com.example.kombatgame.dto;

public class StrategyValidationDTO {
    public boolean valid;
    public String message;
    
    public StrategyValidationDTO() {}
    
    public StrategyValidationDTO(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}
