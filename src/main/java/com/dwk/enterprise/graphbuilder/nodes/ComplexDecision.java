package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.rules.Rule;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class ComplexDecision extends DecisionNode implements Node {

  private final String ruleRef;
  private final Map<String, String> options;
  private final Rule rule;

  @Builder
  public ComplexDecision(
          String id, List<String> dataRefPath, String ruleRef, Map<String, String> options, Rule rule) {
    super(id, dataRefPath);
    this.ruleRef = ruleRef;
    this.options = options;
    this.rule = rule;
  }


  @Override
  public String getNextNodeId(String data) {
    return rule.getNextNode(data, options);
  }
}
