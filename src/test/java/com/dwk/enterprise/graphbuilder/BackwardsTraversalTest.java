package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class BackwardsTraversalTest {
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

    @BeforeEach
    void init() {
        graphLoader.createGraph("test", JsonLoaderForTest.getGraphJsonFromResourcesFolder("test"));
        graph = graphLoader.getGraph("test");
    }

    @Test
    void testPreviousNodeStart() {
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeA", testJson).nextNodeId();
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeStandard() {
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeB", testJson).nextNodeId();
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeDecision() {
        String nextNode = TraverseGraph.getPreviousNode(graph, "endNode", testJson).nextNodeId();
        Assertions.assertEquals("nodeL", nextNode);
    }

}