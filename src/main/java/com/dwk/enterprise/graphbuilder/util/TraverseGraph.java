package com.dwk.enterprise.graphbuilder.util;

import com.dwk.enterprise.graphbuilder.nodes.DecisionNode;
import com.dwk.enterprise.graphbuilder.nodes.Node;
import java.util.Map;

public abstract class TraverseGraph {

  public static String getNextNode(Map<String, Node> graph, String nodeName, String data) {
    return getNextStandardNode(graph, nodeName, data);
  }

  public static String getPreviousNode(Map<String, Node> graph, String nodeName, String data) {
    String startingNode = getFirstNodeInGraph(graph);

    return getBackNode(graph, startingNode, nodeName, data);
  }

  private static String getFirstNodeInGraph(Map<String, Node> graph) {
    return graph.keySet().stream().findFirst().orElseThrow();
  }

  private static String getNextStandardNode(
      Map<String, Node> graph, String currentNode, String data) {
    Node node = graph.get(currentNode);
    String nextNodeId = node.nextNode(data);
    Node nextNode = graph.get(nextNodeId);
    return nextNode instanceof DecisionNode
        ? getNextStandardNode(graph, nextNodeId, data)
        : nextNodeId;
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
    String nextNode = node.nextNode(data);
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
