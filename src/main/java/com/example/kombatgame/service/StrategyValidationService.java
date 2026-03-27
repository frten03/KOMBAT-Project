package com.example.kombatgame.service;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.parser.Parser;
import com.example.kombatgame.strategy.tokenizer.Tokenizer;
import org.springframework.stereotype.Service;

@Service
public class StrategyValidationService {

    public ValidationResult validate(String strategy) {
        if (strategy == null || strategy.trim().isEmpty()) {
            return new ValidationResult(false, "Strategy cannot be empty");
        }

        try {
            Tokenizer tokenizer = new Tokenizer(strategy);
            Parser parser = new Parser(tokenizer);
            Statement stmt = parser.parseStrategy();
            
            System.out.println("[DEBUG] Parsed strategy, root class: " + (stmt != null ? stmt.getClass().getSimpleName() : "null"));
            
            if (stmt == null) {
                return new ValidationResult(false, "Strategy parsed to null");
            }

            boolean hasAction = checkForActions(stmt);
            System.out.println("[DEBUG] hasAction: " + hasAction);
            
            if (!hasAction) {
                return new ValidationResult(false, "Strategy must contain at least one action (move, shoot, or done)");
            }

            return new ValidationResult(true, "Valid");
        } catch (Exception e) {
            System.out.println("[DEBUG] Validation exception: " + e.getMessage());
            e.printStackTrace();
            return new ValidationResult(false, "Parse error: " + e.getMessage());
        }
    }

    private boolean checkForActions(Statement stmt) {
        if (stmt == null) return false;
        
        String className = stmt.getClass().getSimpleName();
        System.out.println("[DEBUG] Checking class: " + className);
        
        if ("MoveStmt".equals(className) || 
            "ShootStmt".equals(className) || 
            "DoneStmt".equals(className)) {
            System.out.println("[DEBUG] Found action: " + className);
            return true;
        }
        
        if ("BlockStmt".equals(className)) {
            try {
                var stmts = stmt.getClass().getMethod("getStatements").invoke(stmt);
                if (stmts instanceof Iterable<?> list) {
                    for (Object s : list) {
                        if (s instanceof Statement) {
                            if (checkForActions((Statement) s)) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] BlockStmt error: " + e.getMessage());
            }
        }
        
        if ("IfStmt".equals(className)) {
            try {
                var thenStmt = stmt.getClass().getMethod("getThenStatement").invoke(stmt);
                var elseStmt = stmt.getClass().getMethod("getElseStatement").invoke(stmt);
                
                if (thenStmt instanceof Statement && checkForActions((Statement) thenStmt)) {
                    return true;
                }
                if (elseStmt instanceof Statement && checkForActions((Statement) elseStmt)) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] IfStmt error: " + e.getMessage());
            }
        }
        
        if ("WhileStmt".equals(className)) {
            try {
                var body = stmt.getClass().getMethod("getBody").invoke(stmt);
                if (body instanceof Statement && checkForActions((Statement) body)) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] WhileStmt error: " + e.getMessage());
            }
        }
        
        return false;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
