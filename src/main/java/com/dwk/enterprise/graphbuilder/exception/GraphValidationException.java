package com.dwk.enterprise.graphbuilder.exception;

import java.util.List;

public class GraphValidationException extends RuntimeException {
    
    private final List<String> validationErrors;
    
    public GraphValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }
    
    public GraphValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
    
    public GraphValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = List.of(message);
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
} 