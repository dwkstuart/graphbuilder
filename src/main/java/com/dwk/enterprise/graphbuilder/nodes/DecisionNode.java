package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.rules.Rule;
import lombok.Builder;

import java.util.Map;


@Builder
public class DecisionNode implements Node {

    private String id;
    private Map<String, String> options;
    private Rule rule;

    @Override
    public String nextNode(Map<String, Object> data) {
        return rule.getNextNode(data, options);
    }
}
