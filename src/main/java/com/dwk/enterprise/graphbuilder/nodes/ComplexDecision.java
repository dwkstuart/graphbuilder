package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.rule.CustomRule;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class ComplexDecision extends DecisionNode implements Node {

  private final String ruleRef;
  private final Map<String, String> options;
  private final CustomRule customRule;

  @Builder
  public ComplexDecision(
          String id, List<String> dataRefPath, String ruleRef, Map<String, String> options, CustomRule customRule) {
    super(id, dataRefPath);
    this.ruleRef = ruleRef;
    this.options = options;
    this.customRule = customRule;
  }

  @Override
  public String getNextNodeId(String data) {
    String keyForOptions = customRule.getKeyForOptions(data);
    return options.get(keyForOptions);
  }
}
