package com.dwk.enterprise.graphbuilder.nodes;

import lombok.Builder;

public class TerminalNode implements Node {

  private final String id;
  private final String exitRef;

  @Builder
  public TerminalNode(String id, String exitRef) {
    this.id = id;
    this.exitRef = exitRef;
  }


  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getNextNodeId(String data) {
    return id;
  }

  public String getExitRef() {
    return exitRef;
  }
}
