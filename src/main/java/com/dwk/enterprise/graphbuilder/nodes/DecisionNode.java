package com.dwk.enterprise.graphbuilder.nodes;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DecisionNode {

  private String id;
  private List<String> dataRefPath;

  public String getId() {
    return id;
  }

  public List<String> getDataRefPath() {
    return dataRefPath;
  }
}
