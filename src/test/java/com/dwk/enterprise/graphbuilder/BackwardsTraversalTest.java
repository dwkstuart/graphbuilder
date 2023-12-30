package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.interfaces.Customer;
import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
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
        Customer customer = new Customer();
        customer.setAge(20);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeA", data);
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeStandard() {
        Customer customer = new Customer();
        customer.setAge(20);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeA", nextNode);
    }

    @Test
    void testPreviousNodeDecision() {
        Customer customer = new Customer();
        customer.setAge(21);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getPreviousNode(graph, "nodeC", data);
        Assertions.assertEquals("nodeB", nextNode);
    }

}