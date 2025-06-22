package com.dwk.enterprise.graphbuilder.validation;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.exception.GraphValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GraphValidationService {

    private final ObjectMapper objectMapper;
    private final JsonSchema schema;
    
    public GraphValidationService(ObjectMapper objectMapper) throws IOException, ProcessingException {
        this.objectMapper = objectMapper;
        this.schema = loadSchema();
    }
    
    /**
     * Validates a graph configuration JSON string against the schema and business rules
     * 
     * @param graphJson JSON string representing the graph configuration
     * @throws GraphValidationException if validation fails
     */
    public void validateGraphJson(String graphJson) {
        log.debug("Validating graph JSON configuration");
        
        List<String> errors = new ArrayList<>();
        
        try {
            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(graphJson);
            
            // Schema validation
            ProcessingReport report = schema.validate(jsonNode);
            if (!report.isSuccess()) {
                report.forEach(processingMessage -> 
                    errors.add("Schema validation error: " + processingMessage.getMessage())
                );
            }
            
            // Business logic validation
            GraphDto graphDto = objectMapper.treeToValue(jsonNode, GraphDto.class);
            if (graphDto != null) {
                errors.addAll(validateBusinessRules(graphDto));
            }
            
        } catch (Exception e) {
            errors.add("JSON parsing error: " + e.getMessage());
        }
        
        if (!errors.isEmpty()) {
            log.error("Graph validation failed with {} errors", errors.size());
            throw new GraphValidationException("Graph validation failed", errors);
        }
        
        log.debug("Graph validation completed successfully");
    }
    
    /**
     * Validates a GraphDto object against business rules
     * 
     * @param graphDto the graph configuration to validate
     * @return list of validation errors
     */
    public List<String> validateBusinessRules(GraphDto graphDto) {
        List<String> errors = new ArrayList<>();
        
        if (graphDto.getFlow() == null || graphDto.getFlow().isEmpty()) {
            errors.add("Graph must contain at least one node");
            return errors;
        }
        
        // Collect all node IDs and validate uniqueness
        Set<String> nodeIds = new HashSet<>();
        Map<String, NodeDto> nodeMap = new HashMap<>();
        
        for (NodeDto node : graphDto.getFlow()) {
            if (node.getId() == null || node.getId().trim().isEmpty()) {
                errors.add("Node ID cannot be null or empty");
                continue;
            }
            
            if (!nodeIds.add(node.getId())) {
                errors.add("Duplicate node ID found: " + node.getId());
            }
            
            nodeMap.put(node.getId(), node);
        }
        
        // Validate node references
        for (NodeDto node : graphDto.getFlow()) {
            errors.addAll(validateNodeReferences(node, nodeMap));
        }
        
        // Validate graph connectivity
        errors.addAll(validateGraphConnectivity(graphDto.getFlow(), nodeMap));
        
        // Validate terminal nodes
        errors.addAll(validateTerminalNodes(graphDto.getFlow()));
        
        return errors;
    }
    
    /**
     * Validates node references and ensures they point to existing nodes
     */
    private List<String> validateNodeReferences(NodeDto node, Map<String, NodeDto> nodeMap) {
        List<String> errors = new ArrayList<>();
        
        // Validate 'next' reference
        if (node.getNext() != null && !nodeMap.containsKey(node.getNext())) {
            errors.add(String.format("Node '%s' references non-existent next node: %s", 
                node.getId(), node.getNext()));
        }
        
        // Validate options references
        if (node.getOptions() != null) {
            for (Map.Entry<String, String> option : node.getOptions().entrySet()) {
                if (!nodeMap.containsKey(option.getValue())) {
                    errors.add(String.format("Node '%s' option '%s' references non-existent node: %s", 
                        node.getId(), option.getKey(), option.getValue()));
                }
            }
        }
        
        return errors;
    }
    
    /**
     * Validates that the graph is properly connected and has no unreachable nodes
     */
    private List<String> validateGraphConnectivity(List<NodeDto> nodes, Map<String, NodeDto> nodeMap) {
        List<String> errors = new ArrayList<>();
        
        // Find starting nodes (nodes that are not referenced by any other node)
        Set<String> referencedNodes = new HashSet<>();
        for (NodeDto node : nodes) {
            if (node.getNext() != null) {
                referencedNodes.add(node.getNext());
            }
            if (node.getOptions() != null) {
                referencedNodes.addAll(node.getOptions().values());
            }
        }
        
        // Check for unreachable nodes
        for (NodeDto node : nodes) {
            if (!referencedNodes.contains(node.getId()) && 
                node.getNodeType() != com.dwk.enterprise.graphbuilder.data.NodeType.TERMINAL_NODE) {
                errors.add(String.format("Node '%s' is unreachable (no other node references it)", 
                    node.getId()));
            }
        }
        
        // Check for circular references (basic check)
        errors.addAll(detectCircularReferences(nodes, nodeMap));
        
        return errors;
    }
    
    /**
     * Detects circular references in the graph
     */
    private List<String> detectCircularReferences(List<NodeDto> nodes, Map<String, NodeDto> nodeMap) {
        List<String> errors = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (NodeDto node : nodes) {
            if (!visited.contains(node.getId())) {
                if (hasCircularReference(node.getId(), nodeMap, visited, recursionStack)) {
                    errors.add(String.format("Circular reference detected involving node: %s", node.getId()));
                }
            }
        }
        
        return errors;
    }
    
    /**
     * DFS to detect circular references
     */
    private boolean hasCircularReference(String nodeId, Map<String, NodeDto> nodeMap, 
                                       Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(nodeId)) {
            return true;
        }
        
        if (visited.contains(nodeId)) {
            return false;
        }
        
        visited.add(nodeId);
        recursionStack.add(nodeId);
        
        NodeDto node = nodeMap.get(nodeId);
        if (node != null) {
            // Check 'next' reference
            if (node.getNext() != null && hasCircularReference(node.getNext(), nodeMap, visited, recursionStack)) {
                return true;
            }
            
            // Check options references
            if (node.getOptions() != null) {
                for (String nextNodeId : node.getOptions().values()) {
                    if (hasCircularReference(nextNodeId, nodeMap, visited, recursionStack)) {
                        return true;
                    }
                }
            }
        }
        
        recursionStack.remove(nodeId);
        return false;
    }
    
    /**
     * Validates that terminal nodes are properly configured
     */
    private List<String> validateTerminalNodes(List<NodeDto> nodes) {
        List<String> errors = new ArrayList<>();
        
        for (NodeDto node : nodes) {
            if (node.getNodeType() == com.dwk.enterprise.graphbuilder.data.NodeType.TERMINAL_NODE) {
                if (node.getExitRef() == null || node.getExitRef().trim().isEmpty()) {
                    errors.add(String.format("Terminal node '%s' must have an exitRef", node.getId()));
                }
                
                if (node.getNext() != null) {
                    errors.add(String.format("Terminal node '%s' should not have a 'next' reference", node.getId()));
                }
                
                if (node.getOptions() != null && !node.getOptions().isEmpty()) {
                    errors.add(String.format("Terminal node '%s' should not have options", node.getId()));
                }
            }
        }
        
        return errors;
    }
    
    /**
     * Loads the JSON schema from the classpath
     */
    private JsonSchema loadSchema() throws IOException, ProcessingException {
        ClassPathResource resource = new ClassPathResource("graph-schema.json");
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode schemaNode = objectMapper.readTree(inputStream);
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            return factory.getJsonSchema(schemaNode);
        }
    }
    
    /**
     * Validates a GraphDto object (convenience method)
     */
    public void validateGraphDto(GraphDto graphDto) {
        List<String> errors = validateBusinessRules(graphDto);
        if (!errors.isEmpty()) {
            throw new GraphValidationException("Graph validation failed", errors);
        }
    }
} 