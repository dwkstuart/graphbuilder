package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TraverseGraphTest {

    GraphLoader graphLoader = new GraphLoader();
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
        graphLoader.createGraph("test", JsonLoaderForTest.getGraphJsonFromResourcesFolder("test"));
        graph = graphLoader.getGraph("test");
    }

    @Test
    void testGetNextNode() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeA", testJson).nextNodeId();
        Assertions.assertEquals("nodeB", nextNode);
    }

    @Test
    void testDecisionNodeA() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", testJson).nextNodeId();
        Assertions.assertEquals("nodeC", nextNode);
    }

    @Test
    void testDecisionNodeB() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", testJsonFalse).nextNodeId();
        Assertions.assertEquals("nodeD", nextNode);
    }

    @Test
    void testBinaryCompareStringTrue() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeG", testJson).nextNodeId();
        Assertions.assertEquals("nodeH", nextNode);
    }

    @Test
    void testBinaryCompareFalse() {
        NodeResponseRecord nodeResponse = TraverseGraph.getNextNode(graph, "nodeG", testJsonFalse);
        Assertions.assertEquals("exit", nodeResponse.nextNodeId());
        Assertions.assertTrue(nodeResponse.graphTraversed());
    }

    @Test
    void testBinaryCompareIntTrue() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeH", testJsonFalse).nextNodeId();
        Assertions.assertEquals("nodeK", nextNode);
    }

    @Test
    void testVisitedNodesEnd() {
        List<String> nodesVisited = TraverseGraph.nodesVisited(graph, "endNode", testJsonFalse);
        System.out.println(nodesVisited);
        Assertions.assertNotEquals(0, nodesVisited.size());
    }

    @Test
    void testVisitedNodesNotFound() {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> TraverseGraph.nodesVisited(graph, "nodeC", testJsonFalse));
        Assertions.assertEquals("Node to check never visited", thrown.getMessage());
    }
}