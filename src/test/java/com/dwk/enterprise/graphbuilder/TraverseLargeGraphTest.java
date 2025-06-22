package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

class TraverseLargeGraphTest {

    GraphLoader graphLoader = new GraphLoader();
    Map<String, Node> graph;

    @BeforeEach
    void init() {
        graphLoader.createGraph("test", JsonLoaderForTest.getGraphJsonFromResourcesFolder("financial-questionnaire"));
        graph = graphLoader.getGraph("test");
    }



    @Test
    void testVisitedNodesEnd() {
        String graphJsonFromResourcesFolder = JsonLoaderForTest.getGraphJsonFromResourcesFolder("financial-questionnaire-sample-data");
        ObjectMapper mapper = new ObjectMapper();
        String ref = StringUtils.collectionToDelimitedString(List.of("sample_clients", "ultra_high_net_worth_client"), "/");
        JsonPointer jsonPointer = JsonPointer.compile("/" + ref);
        JsonNode node1;
        String testString;
        try {

            JsonNode node = mapper.readTree(graphJsonFromResourcesFolder);
            node1 = node.at(jsonPointer);
            testString = node1.toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<String> nodesVisited = TraverseGraph.nodesVisited(graph, "private_wealth_management", testString);
        System.out.println(nodesVisited);
        Assertions.assertNotEquals(0, nodesVisited.size());
    }

    @Test
    void testVisitedNodesDifferent() {
        String graphJsonFromResourcesFolder = JsonLoaderForTest.getGraphJsonFromResourcesFolder("financial-questionnaire-sample-data");
        ObjectMapper mapper = new ObjectMapper();
        String ref = StringUtils.collectionToDelimitedString(List.of("sample_clients", "standard_income_client"), "/");
        JsonPointer jsonPointer = JsonPointer.compile("/" + ref);
        JsonNode node1;
        String testString;
        try {

            JsonNode node = mapper.readTree(graphJsonFromResourcesFolder);
            node1 = node.at(jsonPointer);
            testString = node1.toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        NodeResponseRecord nodesVisited = TraverseGraph.getNextNode(graph, "welcome", testString);
        System.out.println(nodesVisited);
        Assertions.assertNotEquals(0, nodesVisited.nextNodeId());
    }


}