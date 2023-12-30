package com.dwk.enterprise.graphbuilder.nodes;

import java.util.Map;

public interface Node {

    String nextNode(Map<String, Object> data);
}
