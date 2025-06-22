package com.dwk.enterprise.graphbuilder.util;


import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.nodes.*;
import com.dwk.enterprise.graphbuilder.rule.CustomRule;
import com.dwk.enterprise.graphbuilder.validation.GraphValidationService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class GraphLoader {

    private final GraphValidationService validationService;
    private Map<String, Map<String, Node>> graphs;
    private Map<String, CustomRule> customRuleMap;

    public GraphLoader(GraphValidationService validationService) {
        this.validationService = validationService;
    }

    public void createGraph(String graphName, String graphJson) {
        log.info("Creating graph: {}", graphName);
        
        // Validate the graph configuration
        try {
            validationService.validateGraphJson(graphJson);
            log.debug("Graph validation passed for: {}", graphName);
        } catch (Exception e) {
            log.error("Graph validation failed for {}: {}", graphName, e.getMessage());
            throw e;
        }
        
        Gson gson = new Gson();
        GraphDto dtoList = gson.fromJson(graphJson, GraphDto.class);

        List<NodeDto> nodeDtoList = dtoList.getFlow();
        Map<String, Node> nodeMap = new LinkedHashMap<>();

        for (NodeDto nodeDto : nodeDtoList) {
            switch (nodeDto.getNodeType()) {
                case COMPLEX_DECISION_NODE -> decisionNodeAdd(nodeMap, nodeDto, customRuleMap);
                case STANDARD_NODE -> standardNodeAdd(nodeMap, nodeDto);
                case BINARY_CHOICE_NODE -> binaryDecisionNodeAdd(nodeMap, nodeDto);
                case LIST_CHOICE_NODE -> listDecisionNodeAdd(nodeMap, nodeDto);
                case TERMINAL_NODE -> terminalNodeAdd(nodeMap, nodeDto);
                default -> standardNodeAdd(nodeMap, nodeDto);
            }
        }
        if (graphs == null) {
            graphs = new HashMap<>();
        }
        graphs.put(graphName, nodeMap);
        log.info("Graph '{}' created successfully with {} nodes", graphName, nodeMap.size());

    }

    public void createGraph(String graphName, String graphJson, Map<String, CustomRule> customRuleMap) {
        log.info("Creating graph: {} with custom rules", graphName);
        
        // Validate the graph configuration
        try {
            validationService.validateGraphJson(graphJson);
            log.debug("Graph validation passed for: {}", graphName);
        } catch (Exception e) {
            log.error("Graph validation failed for {}: {}", graphName, e.getMessage());
            throw e;
        }
        
        Gson gson = new Gson();
        GraphDto dtoList = gson.fromJson(graphJson, GraphDto.class);

        List<NodeDto> nodeDtoList = dtoList.getFlow();
        Map<String, Node> nodeMap = new LinkedHashMap<>();

        for (NodeDto nodeDto : nodeDtoList) {
            switch (nodeDto.getNodeType()) {
                case COMPLEX_DECISION_NODE -> decisionNodeAdd(nodeMap, nodeDto, customRuleMap);
                case BINARY_CHOICE_NODE -> binaryDecisionNodeAdd(nodeMap, nodeDto);
                case LIST_CHOICE_NODE -> listDecisionNodeAdd(nodeMap, nodeDto);
                case TERMINAL_NODE -> terminalNodeAdd(nodeMap, nodeDto);
                default -> standardNodeAdd(nodeMap, nodeDto);
            }
        }
        if (graphs == null) {
            graphs = new HashMap<>();
        }
        graphs.put(graphName, nodeMap);
        log.info("Graph '{}' created successfully with {} nodes and custom rules", graphName, nodeMap.size());

    }


    public Map<String, Node> getGraph(String graphName) {
        Map<String, Node> graph = graphs.get(graphName);
        if (graph == null) {
            log.warn("Graph '{}' not found", graphName);
        }
        return graph;
    }


    private void standardNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        StandardNode standardNode = StandardNode.builder().id(nodeDto.getId()).nextNode(nodeDto.getNext()).build();
        nodeMap.put(nodeDto.getId(), standardNode);
    }

    private void decisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto, Map<String, CustomRule> customRuleMap) {
        ComplexDecision decisionNode =
                ComplexDecision
                        .builder()
                        .id(nodeDto.getId())
                        .options(nodeDto.getOptions())
                        .ruleRef(nodeDto.getRuleName())
                        .customRule(customRuleMap.get(nodeDto.getRuleName()))
                        .build();
        nodeMap.put(nodeDto.getId(), decisionNode);
    }

    private void binaryDecisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        BinaryChoiceNode binaryChoiceNode =
                BinaryChoiceNode
                        .builder()
                        .id(nodeDto.getId())
                        .options(nodeDto.getOptions())
                        .dataRefPath(nodeDto.getDataRefPath())
                        .comparator(getValueToCompare(nodeDto))
                        .operand(nodeDto.getOperand())
                        .build();
        nodeMap.put(nodeDto.getId(), binaryChoiceNode);
    }

    private void listDecisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        ListChoiceNode listChoiceNode =
                ListChoiceNode
                        .builder()
                        .id(nodeDto.getId())
                        .options(nodeDto.getOptions())
                        .dataRefPath(nodeDto.getDataRefPath())
                        .build();
        nodeMap.put(nodeDto.getId(), listChoiceNode);
    }

    private void terminalNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        TerminalNode terminalNode = TerminalNode.builder()
                .id(nodeDto.getId())
                .exitRef(nodeDto.getExitRef())
                .build();
        nodeMap.put(nodeDto.getId(), terminalNode);
    }

    private Object getValueToCompare(NodeDto nodeDto) {
        if (null != nodeDto.getDoubleValueToCompare()) return nodeDto.getDoubleValueToCompare();
        else if (null != nodeDto.getIntValueToCompare()) return nodeDto.getIntValueToCompare();
        else if (null != nodeDto.getStringValueToCompare()) return nodeDto.getStringValueToCompare();
        return "";
    }


}
