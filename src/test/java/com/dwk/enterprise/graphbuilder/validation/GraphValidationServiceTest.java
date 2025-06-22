package com.dwk.enterprise.graphbuilder.validation;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.data.NodeType;
import com.dwk.enterprise.graphbuilder.data.Operand;
import com.dwk.enterprise.graphbuilder.exception.GraphValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GraphValidationServiceTest {

    private GraphValidationService validationService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        validationService = new GraphValidationService(objectMapper);
    }

    @Test
    void testValidGraphJson() {
        String validJson = """
            {
              "flow": [
                {
                  "id": "nodeA",
                  "next": "nodeB"
                },
                {
                  "id": "nodeB",
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

        assertDoesNotThrow(() -> validationService.validateGraphJson(validJson));
    }

    @Test
    void testValidBinaryChoiceNode() {
        String validJson = """
            {
              "flow": [
                {
                  "id": "start",
                  "next": "decision"
                },
                {
                  "id": "decision",
                  "nodeType": "BINARY_CHOICE_NODE",
                  "options": {
                    "TRUE": "pathA",
                    "FALSE": "pathB"
                  },
                  "stringValueToCompare": "test",
                  "dataRefPath": ["Customer", "name"],
                  "operand": "EQUALS"
                },
                {
                  "id": "pathA",
                  "next": "end"
                },
                {
                  "id": "pathB",
                  "next": "end"
                },
                {
                  "id": "end",
                  "nodeType": "TERMINAL_NODE",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        assertDoesNotThrow(() -> validationService.validateGraphJson(validJson));
    }

    @Test
    void testInvalidJson() {
        String invalidJson = "{ invalid json }";

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("JSON parsing error")));
    }

    @Test
    void testMissingFlow() {
        String invalidJson = "{}";

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("required")));
    }

    @Test
    void testEmptyFlow() {
        String invalidJson = """
            {
              "flow": []
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("at least one node")));
    }

    @Test
    void testDuplicateNodeId() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "nodeA",
                  "next": "nodeB"
                },
                {
                  "id": "nodeA",
                  "next": "endNode"
                },
                {
                  "id": "nodeB",
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

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("Duplicate node ID")));
    }

    @Test
    void testMissingNodeId() {
        String invalidJson = """
            {
              "flow": [
                {
                  "next": "nodeB"
                },
                {
                  "id": "nodeB",
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

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("Node ID cannot be null")));
    }

    @Test
    void testInvalidNodeReference() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "nodeA",
                  "next": "nonExistentNode"
                },
                {
                  "id": "endNode",
                  "nodeType": "TERMINAL_NODE",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("references non-existent next node")));
    }

    @Test
    void testInvalidBinaryChoiceNodeOptions() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "start",
                  "next": "decision"
                },
                {
                  "id": "decision",
                  "nodeType": "BINARY_CHOICE_NODE",
                  "options": {
                    "YES": "pathA",
                    "NO": "pathB"
                  },
                  "stringValueToCompare": "test",
                  "dataRefPath": ["Customer", "name"],
                  "operand": "EQUALS"
                },
                {
                  "id": "pathA",
                  "next": "end"
                },
                {
                  "id": "pathB",
                  "next": "end"
                },
                {
                  "id": "end",
                  "nodeType": "TERMINAL_NODE",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("required")));
    }

    @Test
    void testTerminalNodeWithNext() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "start",
                  "next": "end"
                },
                {
                  "id": "end",
                  "nodeType": "TERMINAL_NODE",
                  "next": "anotherNode",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("should not have a 'next' reference")));
    }

    @Test
    void testTerminalNodeWithoutExitRef() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "start",
                  "next": "end"
                },
                {
                  "id": "end",
                  "nodeType": "TERMINAL_NODE"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("must have an exitRef")));
    }

    @Test
    void testUnreachableNode() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "start",
                  "next": "end"
                },
                {
                  "id": "unreachable",
                  "next": "end"
                },
                {
                  "id": "end",
                  "nodeType": "TERMINAL_NODE",
                  "exitRef": "exit"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("unreachable")));
    }

    @Test
    void testCircularReference() {
        String invalidJson = """
            {
              "flow": [
                {
                  "id": "nodeA",
                  "next": "nodeB"
                },
                {
                  "id": "nodeB",
                  "next": "nodeA"
                }
              ]
            }
            """;

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphJson(invalidJson)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("Circular reference detected")));
    }

    @Test
    void testValidGraphDto() {
        GraphDto graphDto = new GraphDto();
        NodeDto startNode = new NodeDto();
        startNode.setId("start");
        startNode.setNext("end");
        
        NodeDto endNode = new NodeDto();
        endNode.setId("end");
        endNode.setNodeType(NodeType.TERMINAL_NODE);
        endNode.setExitRef("exit");
        
        graphDto.setFlow(List.of(startNode, endNode));

        assertDoesNotThrow(() -> validationService.validateGraphDto(graphDto));
    }

    @Test
    void testInvalidGraphDto() {
        GraphDto graphDto = new GraphDto();
        graphDto.setFlow(List.of()); // Empty flow

        GraphValidationException exception = assertThrows(
            GraphValidationException.class,
            () -> validationService.validateGraphDto(graphDto)
        );

        assertTrue(exception.getValidationErrors().stream()
            .anyMatch(error -> error.contains("at least one node")));
    }
} 