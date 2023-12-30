package com.dwk.enterprise.graphbuilder.rules;

import java.util.Map;

public interface Rule {

    String getNextNode(Map<String, Object> data, Map<String, String> options);
}
