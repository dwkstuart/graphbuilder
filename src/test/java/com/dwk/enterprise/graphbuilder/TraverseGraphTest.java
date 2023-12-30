package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.JsonLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class TraverseGraphTest {

    @Autowired
    GraphLoader graphLoader;
    Map<String, Node> graph;

    @BeforeEach
    void init() {
        graph = graphLoader.getGraph(JsonLoader.getGraphJson("testgraph"));
    }

    @Test
    void testGetNextNode() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 20));
        String nextNode = TraverseGraph.getNextNode(graph, "nodeA", data);
        Assertions.assertEquals("nodeB", nextNode);
    }

    @Test
    void testDecisionNodeA() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 21));
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeC", nextNode);
    }

    @Test
    void testDecisionNodeB() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 18));
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeD", nextNode);
    }
}