package com.dwk.enterprise.graphbuilder.nodes;

public interface Node {

  String getId();

  String getNextNodeId(String data);
}
