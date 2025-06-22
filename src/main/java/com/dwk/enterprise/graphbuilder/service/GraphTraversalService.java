package com.dwk.enterprise.graphbuilder.service;

import com.dwk.enterprise.graphbuilder.config.GraphTraversalConfig;
import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.util.TraverseGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class GraphTraversalService {
    
    private static final Logger log = LoggerFactory.getLogger(GraphTraversalService.class);
    
    private final GraphTraversalConfig config;
    private final Map<String, Map<String, Map<String, Node>>> partitionedGraphs = new ConcurrentHashMap<>();
    
    @Autowired
    public GraphTraversalService(GraphTraversalConfig config) {
        this.config = config;
    }
    
    /**
     * Traverse a graph with automatic partitioning for large graphs
     */
    public NodeResponseRecord traverseGraph(String graphName, Map<String, Node> graph, String nodeName, String data) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("Starting graph traversal for graph: {}, node: {}", graphName, nodeName);
            
            // Check if graph needs partitioning
            if (shouldPartitionGraph(graph)) {
                return traversePartitionedGraph(graphName, graph, nodeName, data);
            } else {
                return TraverseGraph.getNextNode(graph, nodeName, data);
            }
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Graph traversal completed in {}ms", duration);
            
            if (duration > config.getTraversalTimeoutMs()) {
                log.warn("Graph traversal took longer than expected: {}ms", duration);
            }
        }
    }
    
    /**
     * Get previous node with automatic partitioning
     */
    public NodeResponseRecord getPreviousNode(String graphName, Map<String, Node> graph, String nodeName, String data) {
        if (shouldPartitionGraph(graph)) {
            return getPreviousNodePartitioned(graphName, graph, nodeName, data);
        } else {
            return TraverseGraph.getPreviousNode(graph, nodeName, data);
        }
    }
    
    /**
     * Get visited nodes with automatic partitioning
     */
    public List<String> getVisitedNodes(String graphName, Map<String, Node> graph, String lastNodeVisited, String data) {
        if (shouldPartitionGraph(graph)) {
            return getVisitedNodesPartitioned(graphName, graph, lastNodeVisited, data);
        } else {
            return TraverseGraph.nodesVisited(graph, lastNodeVisited, data);
        }
    }
    
    /**
     * Pre-partition a graph and cache the partitions
     */
    public void prePartitionGraph(String graphName, Map<String, Node> graph) {
        if (!shouldPartitionGraph(graph)) {
            log.debug("Graph {} does not need partitioning", graphName);
            return;
        }
        
        log.info("Pre-partitioning graph: {} with {} nodes", graphName, graph.size());
        Map<String, Map<String, Node>> partitions = TraverseGraph.partitionGraph(graph);
        partitionedGraphs.put(graphName, partitions);
        
        log.info("Graph {} partitioned into {} partitions", graphName, partitions.size());
        partitions.forEach((partitionName, partition) -> 
            log.debug("Partition {}: {} nodes", partitionName, partition.size()));
    }
    
    /**
     * Clear cached partitions for a graph
     */
    public void clearPartitions(String graphName) {
        partitionedGraphs.remove(graphName);
        log.debug("Cleared partitions for graph: {}", graphName);
    }
    
    /**
     * Get partition information for a graph
     */
    public Map<String, Integer> getPartitionInfo(String graphName) {
        Map<String, Map<String, Node>> partitions = partitionedGraphs.get(graphName);
        if (partitions == null) {
            return Map.of("status", -1); // Not partitioned
        }
        
        Map<String, Integer> info = new java.util.HashMap<>();
        info.put("partitionCount", partitions.size());
        partitions.forEach((name, partition) -> info.put(name, partition.size()));
        return info;
    }
    
    private boolean shouldPartitionGraph(Map<String, Node> graph) {
        return config.isEnablePartitioning() && graph.size() > config.getPartitionSizeThreshold();
    }
    
    private NodeResponseRecord traversePartitionedGraph(String graphName, Map<String, Node> graph, String nodeName, String data) {
        Map<String, Map<String, Node>> partitions = getOrCreatePartitions(graphName, graph);
        
        // Find which partition contains the starting node
        String partitionName = findPartitionContainingNode(partitions, nodeName);
        if (partitionName == null) {
            throw new RuntimeException("Node not found in any partition: " + nodeName);
        }
        
        log.debug("Traversing partition {} for node {}", partitionName, nodeName);
        Map<String, Node> partition = partitions.get(partitionName);
        return TraverseGraph.getNextNode(partition, nodeName, data);
    }
    
    private NodeResponseRecord getPreviousNodePartitioned(String graphName, Map<String, Node> graph, String nodeName, String data) {
        Map<String, Map<String, Node>> partitions = getOrCreatePartitions(graphName, graph);
        
        // For previous node calculation, we need to search across all partitions
        for (Map.Entry<String, Map<String, Node>> entry : partitions.entrySet()) {
            try {
                NodeResponseRecord result = TraverseGraph.getPreviousNode(entry.getValue(), nodeName, data);
                if (!"ERROR".equals(result.nextNodeId())) {
                    log.debug("Found previous node in partition: {}", entry.getKey());
                    return result;
                }
            } catch (Exception e) {
                log.debug("Previous node not found in partition: {}", entry.getKey());
            }
        }
        
        throw new RuntimeException("Previous node not found in any partition");
    }
    
    private List<String> getVisitedNodesPartitioned(String graphName, Map<String, Node> graph, String lastNodeVisited, String data) {
        Map<String, Map<String, Node>> partitions = getOrCreatePartitions(graphName, graph);
        
        // Find which partition contains the target node
        String partitionName = findPartitionContainingNode(partitions, lastNodeVisited);
        if (partitionName == null) {
            throw new RuntimeException("Node not found in any partition: " + lastNodeVisited);
        }
        
        log.debug("Getting visited nodes from partition {} to node {}", partitionName, lastNodeVisited);
        Map<String, Node> partition = partitions.get(partitionName);
        return TraverseGraph.nodesVisited(partition, lastNodeVisited, data);
    }
    
    private Map<String, Map<String, Node>> getOrCreatePartitions(String graphName, Map<String, Node> graph) {
        return partitionedGraphs.computeIfAbsent(graphName, name -> {
            log.info("Creating partitions for graph: {}", name);
            return TraverseGraph.partitionGraph(graph);
        });
    }
    
    private String findPartitionContainingNode(Map<String, Map<String, Node>> partitions, String nodeName) {
        for (Map.Entry<String, Map<String, Node>> entry : partitions.entrySet()) {
            if (entry.getValue().containsKey(nodeName)) {
                return entry.getKey();
            }
        }
        return null;
    }
} 