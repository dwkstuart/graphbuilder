package com.dwk.enterprise.graphbuilder.util;

import com.dwk.enterprise.graphbuilder.data.NodeResponseRecord;
import com.dwk.enterprise.graphbuilder.nodes.DecisionNode;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import com.dwk.enterprise.graphbuilder.nodes.TerminalNode;

import java.util.Map;

public abstract class TraverseGraph {

    public static NodeResponseRecord getNextNode(Map<String, Node> graph, String nodeName, String data) {

        Node node = getNextStandardNode(graph, nodeName, data);
        return node instanceof TerminalNode ?
                new NodeResponseRecord(((TerminalNode) node).getExitRef(), true)
                : new NodeResponseRecord(node.getId(), false);
    }

    public static NodeResponseRecord getPreviousNode(Map<String, Node> graph, String nodeName, String data) {
        String startingNode = getFirstNodeInGraph(graph);

        return new NodeResponseRecord(getBackNode(graph, startingNode, nodeName, data), false);
    }

    private static String getFirstNodeInGraph(Map<String, Node> graph) {
        return graph.keySet().stream().findFirst().orElseThrow();
    }

    private static Node getNextStandardNode(
            Map<String, Node> graph, String currentNodeId, String data) {
        Node currentNode = graph.get(currentNodeId);
        String nextNodeId = currentNode.getNextNodeId(data);
        Node nextNode = graph.get(nextNodeId);
        return nextNode instanceof DecisionNode
                ? getNextStandardNode(graph, nextNodeId, data)
                : nextNode;
    }

    private static String getBackNode(
            Map<String, Node> graph, String currentNode, String nodeToCheck, String data) {

        if (currentNode.equals(nodeToCheck)) {
            return currentNode;
        }
        Node node = graph.get(currentNode);
        if (node == null) {
            return "ERROR";
        }
        String nextNode = node.getNextNodeId(data);
        if (nextNode == null) {
            return "ERROR";
        }
        if (nextNode.equals(nodeToCheck)) {
            if (node instanceof DecisionNode) {
                return getBackNode(graph, getFirstNodeInGraph(graph), currentNode, data);
            }
            return currentNode;
        }
        return getBackNode(graph, nextNode, nodeToCheck, data);
    }
}
