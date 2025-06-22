package com.dwk.enterprise.graphbuilder.util;

import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.DecisionNode;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.nodes.TerminalNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class TraverseGraph {

    // Configuration constants to prevent infinite loops and stack overflow
    private static final int MAX_TRAVERSAL_DEPTH = 1000;
    private static final int MAX_GRAPH_SIZE = 10000;
    private static final int PARTITION_SIZE_THRESHOLD = 1000;

    public static NodeResponseRecord getNextNode(Map<String, Node> graph, String nodeName, String data) {
        validateGraphSize(graph);
        Node node = getNextStandardNodeIterative(graph, nodeName, data);
        return node instanceof TerminalNode ?
                new NodeResponseRecord(((TerminalNode) node).getExitRef(), true)
                : new NodeResponseRecord(node.getId(), false);
    }

    public static NodeResponseRecord getPreviousNode(Map<String, Node> graph, String nodeName, String data) {
        validateGraphSize(graph);
        String startingNode = getFirstNodeInGraph(graph);
        return new NodeResponseRecord(getBackNodeIterative(graph, startingNode, nodeName, data), false);
    }

    public static List<String> nodesVisited(Map<String, Node> graph, String lastNodeVisited, String data) {
        validateGraphSize(graph);
        return getVisitedNodesIterative(graph, lastNodeVisited, data);
    }

    /**
     * Iterative version of getNextStandardNode to avoid stack overflow
     */
    private static Node getNextStandardNodeIterative(
            Map<String, Node> graph, String currentNodeId, String data) {
        
        Node currentNode = graph.get(currentNodeId);
        int depth = 0;
        
        while (currentNode instanceof DecisionNode && depth < MAX_TRAVERSAL_DEPTH) {
            String nextNodeId = currentNode.getNextNodeId(data);
            currentNode = graph.get(nextNodeId);
            depth++;
        }
        
        if (depth >= MAX_TRAVERSAL_DEPTH) {
            throw new RuntimeException("Maximum traversal depth exceeded: " + MAX_TRAVERSAL_DEPTH);
        }
        
        return currentNode;
    }

    /**
     * Iterative version of getBackNode to avoid stack overflow
     */
    private static String getBackNodeIterative(
            Map<String, Node> graph, String startingNode, String nodeToCheck, String data) {
        
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        
        queue.offer(startingNode);
        visited.add(startingNode);
        
        while (!queue.isEmpty()) {
            String currentNode = queue.poll();
            
            if (currentNode.equals(nodeToCheck)) {
                return currentNode;
            }
            
            Node node = graph.get(currentNode);
            if (node == null) {
                continue;
            }
            
            String nextNode = node.getNextNodeId(data);
            if (nextNode == null) {
                continue;
            }
            
            if (nextNode.equals(nodeToCheck)) {
                if (node instanceof DecisionNode) {
                    // For decision nodes, we need to find the path from start
                    return findPathFromStart(graph, startingNode, currentNode, data);
                }
                return currentNode;
            }
            
            if (!visited.contains(nextNode)) {
                visited.add(nextNode);
                queue.offer(nextNode);
                parentMap.put(nextNode, currentNode);
            }
        }
        
        return "ERROR";
    }

    /**
     * Iterative version of getVisitedNodes to avoid stack overflow
     */
    private static List<String> getVisitedNodesIterative(Map<String, Node> graph, String nodeToCheck, String data) {
        List<String> visitedNodes = new ArrayList<>();
        String startingNode = getFirstNodeInGraph(graph);
        visitedNodes.add(startingNode);
        
        String currentNode = startingNode;
        int depth = 0;
        
        while (!currentNode.equals(nodeToCheck) && depth < MAX_TRAVERSAL_DEPTH) {
            Node node = graph.get(currentNode);
            if (node == null) {
                throw new NoSuchElementException("Node not found: " + currentNode);
            }
            
            String nextNode = node.getNextNodeId(data);
            if (nextNode == null) {
                break;
            }
            
            if (nextNode.equals(nodeToCheck)) {
                visitedNodes.add(nextNode);
                return visitedNodes;
            }
            
            visitedNodes.add(nextNode);
            currentNode = nextNode;
            depth++;
            
            if (graph.get(nextNode) instanceof TerminalNode) {
                break;
            }
        }
        
        if (depth >= MAX_TRAVERSAL_DEPTH) {
            throw new RuntimeException("Maximum traversal depth exceeded: " + MAX_TRAVERSAL_DEPTH);
        }
        
        if (!visitedNodes.contains(nodeToCheck)) {
            throw new RuntimeException("Node to check never visited: " + nodeToCheck);
        }
        
        return visitedNodes;
    }

    /**
     * Graph partitioning strategy for large graphs
     */
    public static Map<String, Map<String, Node>> partitionGraph(Map<String, Node> graph) {
        if (graph.size() <= PARTITION_SIZE_THRESHOLD) {
            return Map.of("main", graph);
        }
        
        Map<String, Map<String, Node>> partitions = new HashMap<>();
        Set<String> processed = new HashSet<>();
        
        // Find connected components
        List<Set<String>> components = findConnectedComponents(graph);
        
        for (int i = 0; i < components.size(); i++) {
            Set<String> component = components.get(i);
            String partitionName = "partition_" + i;
            
            Map<String, Node> partition = component.stream()
                    .collect(Collectors.toMap(
                            nodeId -> nodeId,
                            nodeId -> graph.get(nodeId)
                    ));
            
            partitions.put(partitionName, partition);
            processed.addAll(component);
        }
        
        return partitions;
    }

    /**
     * Find connected components in the graph using iterative BFS
     */
    private static List<Set<String>> findConnectedComponents(Map<String, Node> graph) {
        List<Set<String>> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        for (String nodeId : graph.keySet()) {
            if (!visited.contains(nodeId)) {
                Set<String> component = new HashSet<>();
                Queue<String> queue = new LinkedList<>();
                
                queue.offer(nodeId);
                visited.add(nodeId);
                component.add(nodeId);
                
                while (!queue.isEmpty()) {
                    String current = queue.poll();
                    Node node = graph.get(current);
                    
                    if (node != null) {
                        // For simplicity, we'll explore all possible next nodes
                        // In a real implementation, you might want to explore based on sample data
                        try {
                            String nextNode = node.getNextNodeId("{}");
                            if (nextNode != null && graph.containsKey(nextNode) && !visited.contains(nextNode)) {
                                visited.add(nextNode);
                                component.add(nextNode);
                                queue.offer(nextNode);
                            }
                        } catch (Exception e) {
                            // Skip nodes that require specific data
                        }
                    }
                }
                
                components.add(component);
            }
        }
        
        return components;
    }

    /**
     * Traverse a partitioned graph
     */
    public static NodeResponseRecord traversePartitionedGraph(
            Map<String, Map<String, Node>> partitions, 
            String nodeName, 
            String data) {
        
        // Find which partition contains the starting node
        String partitionName = findPartitionContainingNode(partitions, nodeName);
        if (partitionName == null) {
            throw new RuntimeException("Node not found in any partition: " + nodeName);
        }
        
        Map<String, Node> partition = partitions.get(partitionName);
        return getNextNode(partition, nodeName, data);
    }

    /**
     * Find which partition contains a specific node
     */
    private static String findPartitionContainingNode(Map<String, Map<String, Node>> partitions, String nodeName) {
        for (Map.Entry<String, Map<String, Node>> entry : partitions.entrySet()) {
            if (entry.getValue().containsKey(nodeName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Validate graph size to prevent memory issues
     */
    private static void validateGraphSize(Map<String, Node> graph) {
        if (graph.size() > MAX_GRAPH_SIZE) {
            throw new RuntimeException("Graph size exceeds maximum allowed: " + graph.size() + " > " + MAX_GRAPH_SIZE);
        }
    }

    /**
     * Find path from start node to target node using BFS
     */
    private static String findPathFromStart(Map<String, Node> graph, String startNode, String targetNode, String data) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(startNode);
        visited.add(startNode);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(targetNode)) {
                // Reconstruct path to find the node before target
                String node = targetNode;
                while (parentMap.containsKey(node) && !parentMap.get(node).equals(startNode)) {
                    node = parentMap.get(node);
                }
                return node.equals(targetNode) ? startNode : node;
            }
            
            Node node = graph.get(current);
            if (node != null) {
                try {
                    String nextNode = node.getNextNodeId(data);
                    if (nextNode != null && !visited.contains(nextNode)) {
                        visited.add(nextNode);
                        queue.offer(nextNode);
                        parentMap.put(nextNode, current);
                    }
                } catch (Exception e) {
                    // Skip nodes that require specific data
                }
            }
        }
        
        return startNode;
    }

    private static String getFirstNodeInGraph(Map<String, Node> graph) {
        return graph.keySet().stream().findFirst().orElseThrow();
    }
}
