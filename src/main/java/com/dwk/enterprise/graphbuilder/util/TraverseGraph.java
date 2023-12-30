package com.dwk.enterprise.graphbuilder.util;

import com.dwk.enterprise.graphbuilder.nodes.DecisionNode;
import com.dwk.enterprise.graphbuilder.nodes.Node;

import java.util.Map;


public abstract class TraverseGraph {


    public static String getNextNode(Map<String, Node> graph, String nodeName, Map<String, Object> data) {
        Node node = graph.get(nodeName);
        return node.nextNode(data);
    }

    public static String getPreviousNode(Map<String, Node> graph, String nodeName, Map<String, Object> data) {
        String startingNode = getFirstNodeInGraph(graph);

        return getBackNode(graph, startingNode, nodeName, data);
    }

    private static String getFirstNodeInGraph(Map<String, Node> graph) {
        return graph.keySet().stream().findFirst().orElseThrow();
    }

    private static String getBackNode(Map<String, Node> graph, String currentNode, String nodeToCheck, Map<String, Object> data) {

        if (currentNode.equals(nodeToCheck)) {
            return currentNode;
        }
        Node node = graph.get(currentNode);
        if (node == null) {
            return "ERROR";
        }
        String nextNode = node.nextNode(data);
        if (nextNode == null) {
            return "ERROR";
        }
        if (nextNode.equals(nodeToCheck)) {
            if(node instanceof DecisionNode){
                return getBackNode(graph, getFirstNodeInGraph(graph), currentNode, data);
            }
            return currentNode;
        }
        return getBackNode(graph, nextNode, nodeToCheck, data);
    }
}
