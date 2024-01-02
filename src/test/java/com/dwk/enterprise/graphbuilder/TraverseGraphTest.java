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
    String testJson = """
            {
                "Customer": {
                    "firstName": "Bob",
                    "lastName": "Dole",
                    "age": 90,
                    "dataType": "Customer",
                    "addresses": [{"line1": "test"},{"line1": "test2"} ]
                }
            }
            """;
    String testJsonFalse = """
            {
                "Customer": {
                    "firstName": "Jim",
                    "lastName": "Dole",
                    "age": 20,
                    "dataType": "Customer",
                    "addresses": [{"line1": "test"},{"line1": "test2"} ]
                }
            }
            """;

    @BeforeEach
    void init() {
        graphLoader.createGraph("testgraph", JsonLoader.getGraphJson("testgraph"));
        graph = graphLoader.getGraph("testgraph");
    }

    @Test
    void testGetNextNode() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeA", testJson);
        Assertions.assertEquals("nodeB", nextNode);
    }

    @Test
    void testDecisionNodeA() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", testJson);
        Assertions.assertEquals("nodeC", nextNode);
    }

    @Test
    void testDecisionNodeB() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", testJsonFalse);
        Assertions.assertEquals("nodeD", nextNode);
    }

    @Test
    void testBinaryCompareStringTrue() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeG", testJson);
        Assertions.assertEquals("nodeH", nextNode);
    }

    @Test
    void testBinaryCompareFalse() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeG", testJsonFalse);
        Assertions.assertEquals("nodeI", nextNode);
    }

    @Test
    void testBinaryCompareIntTrue() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeH", testJsonFalse);
        Assertions.assertEquals("nodeK", nextNode);
    }
}