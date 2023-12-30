package com.dwk.enterprise.graphbuilder.util;


import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.nodes.BinaryChoiceNode;
import com.dwk.enterprise.graphbuilder.nodes.ComplexDecision;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.nodes.StandardNode;
import com.dwk.enterprise.graphbuilder.rules.Rule;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GraphLoader {

    private final BeanService beanService;
    private Map<String, Map<String, Node>> graphs;

    public void createGraph(String graphName, String graphJson) {
        Gson gson = new Gson();
        GraphDto dtoList = gson.fromJson(graphJson, GraphDto.class);

        List<NodeDto> nodeDtoList = dtoList.getFlow();
        Map<String, Node> nodeMap = new LinkedHashMap<>();

        for (NodeDto nodeDto : nodeDtoList) {
            switch (nodeDto.getNodeType()) {
                case COMPLEX_DECISION_NODE -> decisionNodeAdd(nodeMap, nodeDto);
                case BINARY_CHOICE_NODE -> binaryDecisionNodeAdd(nodeMap, nodeDto);
                default -> standardNodeAdd(nodeMap, nodeDto);
            }
        }
        if (graphs == null) {
            graphs = new HashMap<>();
        }
        graphs.put(graphName, nodeMap);

    }
    public Map<String, Node> getGraph(String graphJson) {
        return graphs.get(graphJson);
    }


    private void standardNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        StandardNode standardNode = StandardNode.builder().id(nodeDto.getId()).nextNode(nodeDto.getNext()).build();
        nodeMap.put(nodeDto.getId(), standardNode);
    }

    private void decisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        Rule rule = (Rule) beanService.getBean(nodeDto.getRuleName());
        ComplexDecision decisionNode = ComplexDecision.builder().id(nodeDto.getId()).options(nodeDto.getOptions()).rule(rule).build();
        nodeMap.put(nodeDto.getId(), decisionNode);
    }

    private void binaryDecisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        BinaryChoiceNode binaryChoiceNode =
                BinaryChoiceNode
                        .builder()
                        .id(nodeDto.getId())
                        .options(nodeDto.getOptions())
                        .dataType(nodeDto.getDataType())
                        .fieldName(nodeDto.getFieldName())
                        .trueValue(nodeDto.getTrueValue())
                        .build();
        nodeMap.put(nodeDto.getId(), binaryChoiceNode);
    }


}
