package com.dwk.enterprise.graphbuilder.controller;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.data.NodeType;
import com.dwk.enterprise.graphbuilder.exception.GraphValidationException;
import com.dwk.enterprise.graphbuilder.validation.GraphValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphValidationControllerTest {

    @Mock
    private GraphValidationService validationService;

    @InjectMocks
    private GraphValidationController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testValidateGraph_Success() {
        // Given
        String validJson = """
            {
              "flow": [
                {
                  "id": "nodeA",
                  "next": "endNode"
                },
                {
                  "id": "endNode",
                  "nodeType": "TERMINAL_NODE",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        doNothing().when(validationService).validateGraphJson(validJson);

        // When
        ResponseEntity<GraphValidationController.ValidationResponse> response = 
            controller.validateGraph(validJson);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isValid());
        assertEquals("Graph validation successful", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
        
        verify(validationService).validateGraphJson(validJson);
    }

    @Test
    void testValidateGraph_ValidationFailure() {
        // Given
        String invalidJson = "{}";
        List<String> errors = List.of("Missing required field: flow", "Graph must contain at least one node");
        
        doThrow(new GraphValidationException("Validation failed", errors))
            .when(validationService).validateGraphJson(invalidJson);

        // When
        ResponseEntity<GraphValidationController.ValidationResponse> response = 
            controller.validateGraph(invalidJson);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        assertEquals("Graph validation failed", response.getBody().getMessage());
        assertEquals(errors, response.getBody().getErrors());
        
        verify(validationService).validateGraphJson(invalidJson);
    }

    @Test
    void testValidateGraph_InternalError() {
        // Given
        String json = "{}";
        
        doThrow(new RuntimeException("Unexpected error"))
            .when(validationService).validateGraphJson(json);

        // When
        ResponseEntity<GraphValidationController.ValidationResponse> response = 
            controller.validateGraph(json);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        assertEquals("Internal server error", response.getBody().getMessage());
        assertEquals(List.of("Unexpected error"), response.getBody().getErrors());
        
        verify(validationService).validateGraphJson(json);
    }

    @Test
    void testValidateGraphDto_Success() {
        // Given
        GraphDto graphDto = new GraphDto();
        NodeDto startNode = new NodeDto();
        startNode.setId("start");
        startNode.setNext("end");
        
        NodeDto endNode = new NodeDto();
        endNode.setId("end");
        endNode.setNodeType(NodeType.TERMINAL_NODE);
        endNode.setExitRef("exit");
        
        graphDto.setFlow(List.of(startNode, endNode));

        doNothing().when(validationService).validateGraphDto(graphDto);

        // When
        ResponseEntity<GraphValidationController.ValidationResponse> response = 
            controller.validateGraphDto(graphDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isValid());
        assertEquals("Graph validation successful", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
        
        verify(validationService).validateGraphDto(graphDto);
    }

    @Test
    void testValidateGraphDto_ValidationFailure() {
        // Given
        GraphDto graphDto = new GraphDto();
        graphDto.setFlow(List.of()); // Empty flow
        
        List<String> errors = List.of("Graph must contain at least one node");
        
        doThrow(new GraphValidationException("Validation failed", errors))
            .when(validationService).validateGraphDto(graphDto);

        // When
        ResponseEntity<GraphValidationController.ValidationResponse> response = 
            controller.validateGraphDto(graphDto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isValid());
        assertEquals("Graph validation failed", response.getBody().getMessage());
        assertEquals(errors, response.getBody().getErrors());
        
        verify(validationService).validateGraphDto(graphDto);
    }

    @Test
    void testGetSchema() {
        // When
        ResponseEntity<java.util.Map<String, String>> response = controller.getSchema();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Graph schema is available at /api/graph/validation/schema", 
            response.getBody().get("message"));
        assertEquals("classpath:graph-schema.json", 
            response.getBody().get("schemaLocation"));
    }
} 