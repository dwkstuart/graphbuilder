package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.rule.CustomRule;
import com.dwk.enterprise.graphbuilder.rule.TestRule;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ComplexRulesTest {

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

    @BeforeEach
    void init() {
        Map<String, CustomRule> customRuleMap = Map.of("TestRule", new TestRule());
        graphLoader.createGraph("test_custom", JsonLoaderForTest.getGraphJsonFromResourcesFolder("test_custom"), customRuleMap);
        graph = graphLoader.getGraph("test_custom");
    }

    @Test
    void testDecisionNodeA() {
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", testJson).nextNodeId();
        Assertions.assertEquals("nodeC", nextNode);
    }

}
