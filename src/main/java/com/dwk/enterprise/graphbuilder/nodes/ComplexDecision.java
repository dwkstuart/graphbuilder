package com.dwk.enterprise.graphbuilder.nodes;

import lombok.Builder;

import java.util.List;
import java.util.Map;

public class ComplexDecision extends DecisionNode implements Node {

  private final String ruleRef;
  private final Map<String, String> options;

  @Builder
  public ComplexDecision(
      String id, List<String> dataRefPath, String ruleRef, Map<String, String> options) {
    super(id, dataRefPath);
    this.ruleRef = ruleRef;
    this.options = options;
  }

  @Override
  public String getNextNodeId(String data) {
    return "";
  }
}
