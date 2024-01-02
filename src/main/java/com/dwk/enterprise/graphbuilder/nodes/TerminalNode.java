package com.dwk.enterprise.graphbuilder.nodes;

import java.util.List;
import lombok.Builder;

public class TerminalNode extends DecisionNode implements Node {

  private final String exitRef;

  @Builder
  public TerminalNode(String id, List<String> dataRefPath, String exitRef) {
    super(id, dataRefPath);
    this.exitRef = exitRef;
  }

  @Override
  public String nextNode(String data) {
    return exitRef;
  }
}
