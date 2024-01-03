package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.util.JsonProcessorUtil;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ListChoiceNode extends DecisionNode implements Node {

  private final Map<String, String> options;

  @Builder
  public ListChoiceNode(String id, List<String> dataRefPath, Map<String, String> options) {
    super(id, dataRefPath);
    this.options = options;
  }

  @Override
  public String getNextNodeId(String data) {
    Optional<Object> valueAtLocation =
            JsonProcessorUtil.getValueAtLocation(data, super.getDataRefPath());
    var value = valueAtLocation.orElseThrow();
    String key = value.toString();
    boolean containsKey = options.containsKey(key);
    if (containsKey) {
      return options.get(key);
    }
    throw new NoSuchElementException();
  }
}
