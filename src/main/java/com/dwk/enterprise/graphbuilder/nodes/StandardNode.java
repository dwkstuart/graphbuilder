package com.dwk.enterprise.graphbuilder.nodes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StandardNode implements Node {

  private String id;
  private String nextNode;

  @Override
  public String nextNode(String data) {
    return nextNode;
  }
}
