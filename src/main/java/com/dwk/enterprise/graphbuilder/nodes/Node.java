package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.interfaces.RulesData;

import java.util.Map;

public interface Node {

    String nextNode(Map<String, RulesData> data);
}
