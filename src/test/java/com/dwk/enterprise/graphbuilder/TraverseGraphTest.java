package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.interfaces.Customer;
import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.dwk.enterprise.graphbuilder.util.JsonLoader;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
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
        Customer customer = new Customer();
        customer.setAge(20);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeA", data);
        Assertions.assertEquals("nodeB", nextNode);
    }

    @Test
    void testDecisionNodeA() {
        Customer customer = new Customer();
        customer.setAge(21);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeC", nextNode);
    }

    @Test
    void testDecisionNodeB() {
        Customer customer = new Customer();
        customer.setAge(20);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeB", data);
        Assertions.assertEquals("nodeD", nextNode);
    }

    @Test
    void testBinaryCompareStringTrue() {
        Customer customer = new Customer();
        customer.setFirstName("Bob");
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeG", data);
        Assertions.assertEquals("nodeH", nextNode);
    }

    @Test
    void testBinaryCompareFalse() {
        Customer customer = new Customer();
        customer.setFirstName("Jim");
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeG", data);
        Assertions.assertEquals("nodeI", nextNode);
    }

    @Test
    void testBinaryCompareIntTrue() {
        Customer customer = new Customer();
        customer.setAge(20);
        Map<String, RulesData> data = Map.of("Customer", customer);
        String nextNode = TraverseGraph.getNextNode(graph, "nodeH", data);
        Assertions.assertEquals("nodeK", nextNode);
    }

    @Test
    void testGetVisitedList() {
        Customer customer = new Customer();
        customer.setAge(20);
        customer.setFirstName("Bob");
        Map<String, RulesData> data = Map.of("Customer", customer);
        List<String> list = TraverseGraph.nodesVisited(graph, "nodeD", data);
        System.out.println(list);
        Assertions.assertEquals(4, list.size());
        Assertions.assertTrue(list.contains("nodeD"));
    }
}