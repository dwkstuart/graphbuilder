package com.dwk.enterprise.graphbuilder.controllers;

import com.dwk.enterprise.graphbuilder.interfaces.Customer;
import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class TraversalController {

    private final GraphLoader graphLoader;

    @PostMapping(value = "/{graphName}/{currentNode}/next", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNextNode(@PathVariable String graphName, @PathVariable String currentNode, @RequestBody Map<String, Map<String, String>> rulesDataMap) {
        Map<String, Node> graph = graphLoader.getGraph(graphName);
        Map<String, RulesData> dataMap = rulesDataMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> transformToRule(e.getValue())));

        return TraverseGraph.getNextNode(graph, currentNode, dataMap);
    }

    private RulesData transformToRule(Map<String, String> e) {
        ObjectMapper mapper = new ObjectMapper();
        return switch (e.get("dataType")) {
            case "Customer" -> mapper.convertValue(e, Customer.class);
            default -> throw new IllegalStateException("Unexpected value: " + e.get("dataType"));
        };

    }
}
