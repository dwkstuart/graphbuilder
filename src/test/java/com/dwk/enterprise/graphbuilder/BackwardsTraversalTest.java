package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.rules.RuleOne;
import com.dwk.enterprise.graphbuilder.util.BeanService;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.JsonLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = {BeanService.class, GraphLoader.class, RuleOne.class})
class BackwardsTraversalTest {
    @Autowired
    GraphLoader graphLoader;
    Map<String, Node> graph;

    @BeforeEach
    void init() {
        graph = graphLoader.getGraph(JsonLoader.getGraphJson("testgraph"));
    }

    @Test
    void testPreviousNodeStart() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 20));
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeA", data);
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeStandard() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 20));
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeDecision() {
        Map<String, Object> data = Map.of("Customer", Map.of("age", 21));
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeC", data);
        Assertions.assertEquals("nodeB", nextNode);
    }

}