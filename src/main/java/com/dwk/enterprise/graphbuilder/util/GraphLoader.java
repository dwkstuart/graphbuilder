package com.dwk.enterprise.graphbuilder.util;


import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.data.NodeDto;
import com.dwk.enterprise.graphbuilder.nodes.DecisionNode;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.nodes.StandardNode;
import com.dwk.enterprise.graphbuilder.rules.Rule;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GraphLoader {

    private final BeanService beanService;

    @Cacheable(cacheNames = "graphs")
    public Map<String, Node> getGraph(String graphJson) {


        Gson gson = new Gson();
        GraphDto dtoList = gson.fromJson(graphJson, GraphDto.class);

        List<NodeDto> nodeDtoList = dtoList.getFlow();
        Map<String, Node> nodeMap = new LinkedHashMap<>();
        for (NodeDto nodeDto : nodeDtoList) {
            if (nodeDto.isDecisionNode()) {
                decisionNodeAdd(nodeMap, nodeDto);
            } else {
                standardNodeAdd(nodeMap, nodeDto);
            }

        }
        return nodeMap;
    }


    private void standardNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        StandardNode standardNode = StandardNode.builder().id(nodeDto.getId()).nextNode(nodeDto.getNext()).build();
        nodeMap.put(nodeDto.getId(), standardNode);
    }

    private void decisionNodeAdd(Map<String, Node> nodeMap, NodeDto nodeDto) {
        Rule rule = (Rule) beanService.getBean(nodeDto.getRuleName());
        DecisionNode decisionNode = DecisionNode.builder().id(nodeDto.getId()).options(nodeDto.getOptions()).rule(rule).build();
        nodeMap.put(nodeDto.getId(), decisionNode);
    }

}