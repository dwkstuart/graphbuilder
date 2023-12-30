package com.dwk.enterprise.graphbuilder.rules;

import com.dwk.enterprise.graphbuilder.interfaces.RulesData;

import java.util.Map;

public interface Rule {

    String getNextNode(Map<String, RulesData> data, Map<String, String> options);
}
