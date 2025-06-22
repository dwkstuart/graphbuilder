package com.dwk.enterprise.graphbuilder.controller;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.exception.GraphValidationException;
import com.dwk.enterprise.graphbuilder.validation.GraphValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/graph/validation")
@RequiredArgsConstructor
public class GraphValidationController {

    private final GraphValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateGraph(@RequestBody String graphJson) {
        log.info("Received graph validation request");
        
        try {
            validationService.validateGraphJson(graphJson);
            return ResponseEntity.ok(new ValidationResponse(true, "Graph validation successful", null));
        } catch (GraphValidationException e) {
            log.warn("Graph validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ValidationResponse(false, "Graph validation failed", e.getValidationErrors()));
        } catch (Exception e) {
            log.error("Unexpected error during validation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new ValidationResponse(false, "Internal server error", List.of(e.getMessage())));
        }
    }

    @PostMapping("/validate-dto")
    public ResponseEntity<ValidationResponse> validateGraphDto(@RequestBody GraphDto graphDto) {
        log.info("Received GraphDto validation request");
        
        try {
            validationService.validateGraphDto(graphDto);
            return ResponseEntity.ok(new ValidationResponse(true, "Graph validation successful", null));
        } catch (GraphValidationException e) {
            log.warn("Graph validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ValidationResponse(false, "Graph validation failed", e.getValidationErrors()));
        } catch (Exception e) {
            log.error("Unexpected error during validation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new ValidationResponse(false, "Internal server error", List.of(e.getMessage())));
        }
    }

    @GetMapping("/schema")
    public ResponseEntity<Map<String, String>> getSchema() {
        log.info("Schema request received");
        return ResponseEntity.ok(Map.of(
            "message", "Graph schema is available at /api/graph/validation/schema",
            "schemaLocation", "classpath:graph-schema.json"
        ));
    }

    public static class ValidationResponse {
        private final boolean valid;
        private final String message;
        private final List<String> errors;

        public ValidationResponse(boolean valid, String message, List<String> errors) {
            this.valid = valid;
            this.message = message;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
} 