package com.dwk.enterprise.graphbuilder;

import com.dwk.enterprise.graphbuilder.config.GraphTraversalConfig;
import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.BinaryChoiceNode;
import com.dwk.enterprise.graphbuilder.nodes.StandardNode;
import com.dwk.enterprise.graphbuilder.nodes.TerminalNode;
import com.dwk.enterprise.graphbuilder.service.GraphTraversalService;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IterativeTraversalTest {

    private GraphTraversalService traversalService;
    private Map<String, com.dwk.enterprise.graphbuilder.nodes.Node> largeGraph;
    private Map<String, com.dwk.enterprise.graphbuilder.nodes.Node> simpleGraph;

    @BeforeEach
    void setUp() {
        GraphTraversalConfig config = new GraphTraversalConfig();
        config.setMaxTraversalDepth(1000);
        config.setMaxGraphSize(10000);
        config.setPartitionSizeThreshold(10); // Low threshold for testing
        config.setEnablePartitioning(true);
        
        traversalService = new GraphTraversalService(config);
        
        createSimpleGraph();
        createLargeGraph();
    }

    @Test
    void testIterativeTraversalSimpleGraph() {
        // Test that iterative traversal works the same as recursive for small graphs
        String data = "{\"customer\": {\"age\": 25}}";
        
        NodeResponseRecord result = TraverseGraph.getNextNode(simpleGraph, "start", data);
        
        assertNotNull(result);
        assertEquals("node2", result.nextNodeId());
        assertFalse(result.graphTraversed());
    }

    @Test
    void testLargeGraphPartitioning() {
        // Test that large graphs are automatically partitioned
        Map<String, Map<String, com.dwk.enterprise.graphbuilder.nodes.Node>> partitions = 
            TraverseGraph.partitionGraph(largeGraph);
        
        assertTrue(partitions.size() > 1, "Large graph should be partitioned");
        
        // Verify each partition is smaller than the threshold
        partitions.values().forEach(partition -> 
            assertTrue(partition.size() <= 10, "Each partition should be within size limit"));
    }

    @Test
    void testTraversalServiceWithPartitioning() {
        String data = "{\"customer\": {\"age\": 25}}";
        
        // Test traversal through the service (which handles partitioning automatically)
        NodeResponseRecord result = traversalService.traverseGraph("test-graph", largeGraph, "start", data);
        
        assertNotNull(result);
        assertFalse(result.graphTraversed());
    }

    @Test
    void testPrePartitioning() {
        // Test pre-partitioning functionality
        traversalService.prePartitionGraph("test-graph", largeGraph);
        
        Map<String, Integer> partitionInfo = traversalService.getPartitionInfo("test-graph");
        assertTrue(partitionInfo.get("partitionCount") > 1);
    }

    @Test
    void testVisitedNodesIterative() {
        String data = "{\"customer\": {\"age\": 25}}";
        
        List<String> visited = TraverseGraph.nodesVisited(simpleGraph, "node2", data);
        
        assertNotNull(visited);
        assertTrue(visited.contains("start"));
        assertTrue(visited.contains("node2"));
    }

    @Test
    void testPreviousNodeIterative() {
        String data = "{\"customer\": {\"age\": 25}}";
        
        NodeResponseRecord result = TraverseGraph.getPreviousNode(simpleGraph, "node2", data);
        
        assertNotNull(result);
        assertEquals("start", result.nextNodeId());
    }

    @Test
    void testMaxDepthProtection() {
        // Create a graph that would cause deep recursion
        Map<String, com.dwk.enterprise.graphbuilder.nodes.Node> deepGraph = createDeepGraph();
        
        String data = "{\"customer\": {\"age\": 25}}";
        
        // This should not cause stack overflow
        assertThrows(RuntimeException.class, () -> {
            TraverseGraph.getNextNode(deepGraph, "start", data);
        });
    }

    @Test
    void testPartitionedGraphTraversal() {
        String data = "{\"customer\": {\"age\": 25}}";
        
        // Test traversing a partitioned graph
        NodeResponseRecord result = TraverseGraph.traversePartitionedGraph(
            TraverseGraph.partitionGraph(largeGraph), "start", data);
        
        assertNotNull(result);
    }

    private void createSimpleGraph() {
        simpleGraph = new HashMap<>();
        
        // Create a simple linear graph: start -> node1 -> node2 -> terminal
        BinaryChoiceNode start = BinaryChoiceNode.builder()
            .id("start")
            .dataRefPath(Arrays.asList("customer", "age"))
            .options(Map.of("TRUE", "node1", "FALSE", "node1"))
            .comparator(18)
            .operand(com.dwk.enterprise.graphbuilder.data.Operand.GREATER_THAN)
            .build();
        
        StandardNode node1 = StandardNode.builder().id("node1").nextNode("node2").build();
        StandardNode node2 = StandardNode.builder().id("node2").nextNode("terminal").build();
        TerminalNode terminal = new TerminalNode("terminal", "exit1");
        
        simpleGraph.put("start", start);
        simpleGraph.put("node1", node1);
        simpleGraph.put("node2", node2);
        simpleGraph.put("terminal", terminal);
    }

    private void createLargeGraph() {
        largeGraph = new HashMap<>();
        
        // Create a large graph with multiple decision nodes
        for (int i = 0; i < 50; i++) {
            String nodeId = "node" + i;
            
            if (i == 0) {
                // Start node
                BinaryChoiceNode start = BinaryChoiceNode.builder()
                    .id(nodeId)
                    .dataRefPath(Arrays.asList("customer", "age"))
                    .options(Map.of("TRUE", "node1", "FALSE", "node25"))
                    .comparator(18)
                    .operand(com.dwk.enterprise.graphbuilder.data.Operand.GREATER_THAN)
                    .build();
                largeGraph.put(nodeId, start);
            } else if (i == 49) {
                // Terminal node
                TerminalNode terminal = new TerminalNode(nodeId, "exit" + i);
                largeGraph.put(nodeId, terminal);
            } else if (i % 5 == 0) {
                // Decision nodes every 5 nodes
                BinaryChoiceNode decision = BinaryChoiceNode.builder()
                    .id(nodeId)
                    .dataRefPath(Arrays.asList("customer", "age"))
                    .options(Map.of("TRUE", "node" + (i + 1), "FALSE", "node" + (i + 2)))
                    .comparator(20 + i)
                    .operand(com.dwk.enterprise.graphbuilder.data.Operand.GREATER_THAN)
                    .build();
                largeGraph.put(nodeId, decision);
            } else {
                // Standard nodes
                StandardNode standard = StandardNode.builder().id(nodeId).nextNode("node" + (i + 1)).build();
                largeGraph.put(nodeId, standard);
            }
        }
    }

    private Map<String, com.dwk.enterprise.graphbuilder.nodes.Node> createDeepGraph() {
        Map<String, com.dwk.enterprise.graphbuilder.nodes.Node> deepGraph = new HashMap<>();
        
        // Create a graph with very deep recursion potential
        for (int i = 0; i < 2000; i++) {
            String nodeId = "deep" + i;
            
            if (i == 0) {
                BinaryChoiceNode start = BinaryChoiceNode.builder()
                    .id(nodeId)
                    .dataRefPath(Arrays.asList("customer", "age"))
                    .options(Map.of("TRUE", "deep1", "FALSE", "deep1"))
                    .comparator(18)
                    .operand(com.dwk.enterprise.graphbuilder.data.Operand.GREATER_THAN)
                    .build();
                deepGraph.put(nodeId, start);
            } else if (i == 1999) {
                TerminalNode terminal = new TerminalNode(nodeId, "exit");
                deepGraph.put(nodeId, terminal);
            } else {
                BinaryChoiceNode decision = BinaryChoiceNode.builder()
                    .id(nodeId)
                    .dataRefPath(Arrays.asList("customer", "age"))
                    .options(Map.of("TRUE", "deep" + (i + 1), "FALSE", "deep" + (i + 1)))
                    .comparator(18)
                    .operand(com.dwk.enterprise.graphbuilder.data.Operand.GREATER_THAN)
                    .build();
                deepGraph.put(nodeId, decision);
            }
        }
        
        return deepGraph;
    }
} 