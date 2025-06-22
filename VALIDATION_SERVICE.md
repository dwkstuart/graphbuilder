# GraphBuilder Validation Service

## Overview

The GraphBuilder Validation Service provides comprehensive validation for graph configurations using JSON Schema validation and business logic validation. It ensures that graph configurations are properly structured and follow all business rules before they are processed.

## Features

### 1. JSON Schema Validation
- Validates graph configuration against a comprehensive JSON schema
- Ensures proper structure and data types
- Validates required fields based on node types
- Enforces naming conventions and patterns

### 2. Business Logic Validation
- **Node Reference Validation**: Ensures all node references point to existing nodes
- **Graph Connectivity**: Validates that all nodes are reachable
- **Circular Reference Detection**: Prevents infinite loops in graph traversal
- **Terminal Node Validation**: Ensures terminal nodes are properly configured
- **Duplicate Node Detection**: Prevents duplicate node IDs

### 3. Node Type-Specific Validation
- **STANDARD_NODE**: Requires `next` field
- **BINARY_CHOICE_NODE**: Requires `options` with TRUE/FALSE keys, `dataRefPath`, and `operand`
- **LIST_CHOICE_NODE**: Requires `options` and `dataRefPath`
- **COMPLEX_DECISION_NODE**: Requires `options` and `ruleName`
- **TERMINAL_NODE**: Requires `exitRef` and no `next` or `options`

## API Endpoints

### Validate Graph JSON
```http
POST /api/graph/validation/validate
Content-Type: application/json

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
```

**Response (Success):**
```json
{
  "valid": true,
  "message": "Graph validation successful",
  "errors": null
}
```

**Response (Failure):**
```json
{
  "valid": false,
  "message": "Graph validation failed",
  "errors": [
    "Node 'nodeA' references non-existent next node: nonExistentNode",
    "Graph must contain at least one node"
  ]
}
```

### Validate GraphDto Object
```http
POST /api/graph/validation/validate-dto
Content-Type: application/json

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
```

### Get Schema Information
```http
GET /api/graph/validation/schema
```

**Response:**
```json
{
  "message": "Graph schema is available at /api/graph/validation/schema",
  "schemaLocation": "classpath:graph-schema.json"
}
```

## JSON Schema

The validation service uses a comprehensive JSON schema located at `src/main/resources/graph-schema.json`. The schema includes:

### Schema Structure
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Graph Configuration Schema",
  "description": "Schema for defining graph-based decision flow configurations",
  "type": "object",
  "required": ["flow"],
  "properties": {
    "flow": {
      "type": "array",
      "description": "Array of nodes defining the graph flow",
      "minItems": 1,
      "items": {
        "$ref": "#/definitions/Node"
      }
    }
  }
}
```

### Node Definition
Each node must have:
- `id`: Unique identifier (required)
- `nodeType`: Type of node (optional, defaults to STANDARD_NODE)
- Additional fields based on node type

### Node Type Requirements

#### STANDARD_NODE
```json
{
  "id": "nodeId",
  "next": "nextNodeId"
}
```

#### BINARY_CHOICE_NODE
```json
{
  "id": "nodeId",
  "nodeType": "BINARY_CHOICE_NODE",
  "options": {
    "TRUE": "nextNodeId",
    "FALSE": "alternativeNodeId"
  },
  "dataRefPath": ["Customer", "age"],
  "operand": "GREATER_THAN",
  "intValueToCompare": 18
}
```

#### LIST_CHOICE_NODE
```json
{
  "id": "nodeId",
  "nodeType": "LIST_CHOICE_NODE",
  "options": {
    "option1": "nextNodeId1",
    "option2": "nextNodeId2"
  },
  "dataRefPath": ["Customer", "preference"]
}
```

#### COMPLEX_DECISION_NODE
```json
{
  "id": "nodeId",
  "nodeType": "COMPLEX_DECISION_NODE",
  "options": {
    "resultA": "nextNodeIdA",
    "resultB": "nextNodeIdB"
  },
  "ruleName": "CustomRuleName"
}
```

#### TERMINAL_NODE
```json
{
  "id": "nodeId",
  "nodeType": "TERMINAL_NODE",
  "exitRef": "exitReference"
}
```

## Usage Examples

### Programmatic Usage

```java
@Service
public class GraphService {
    
    private final GraphValidationService validationService;
    
    public GraphService(GraphValidationService validationService) {
        this.validationService = validationService;
    }
    
    public void createGraph(String graphJson) {
        // Validate before creating
        validationService.validateGraphJson(graphJson);
        
        // Proceed with graph creation
        // ...
    }
    
    public void validateGraphDto(GraphDto graphDto) {
        validationService.validateGraphDto(graphDto);
    }
}
```

### Integration with GraphLoader

The `GraphLoader` class automatically validates graphs when creating them:

```java
GraphLoader graphLoader = new GraphLoader(validationService);
graphLoader.createGraph("myGraph", graphJson); // Validation happens automatically
```

## Error Handling

The validation service throws `GraphValidationException` with detailed error messages:

```java
try {
    validationService.validateGraphJson(graphJson);
} catch (GraphValidationException e) {
    List<String> errors = e.getValidationErrors();
    // Handle validation errors
}
```

## Common Validation Errors

1. **Missing Required Fields**
   - Node ID is null or empty
   - Required fields missing for specific node types

2. **Invalid References**
   - Node references non-existent next node
   - Option values reference non-existent nodes

3. **Graph Structure Issues**
   - Circular references detected
   - Unreachable nodes
   - Empty graph flow

4. **Node Type Violations**
   - Terminal nodes with next references
   - Binary choice nodes without TRUE/FALSE options
   - Missing required fields for specific node types

## Testing

The validation service includes comprehensive tests covering:

- Valid graph configurations
- Invalid JSON syntax
- Missing required fields
- Duplicate node IDs
- Invalid node references
- Circular references
- Unreachable nodes
- Node type-specific validation

Run tests with:
```bash
mvn test -Dtest=GraphValidationServiceTest
mvn test -Dtest=GraphValidationControllerTest
```

## Configuration

The validation service uses Spring Boot's dependency injection. Ensure you have the following beans configured:

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

## Dependencies

The validation service requires:

- `com.github.java-json-tools:json-schema-validator:2.2.14`
- `com.fasterxml.jackson.core:jackson-databind`
- Spring Boot Web (for REST endpoints)
- Lombok (for logging and annotations)

## Performance Considerations

- JSON schema is loaded once at service initialization
- Validation is performed in memory
- Large graphs may impact performance due to circular reference detection
- Consider caching validation results for frequently validated graphs

## Security

- Input validation prevents JSON injection attacks
- Schema validation ensures proper data structure
- Business logic validation prevents malicious graph configurations
- Error messages don't expose sensitive information 