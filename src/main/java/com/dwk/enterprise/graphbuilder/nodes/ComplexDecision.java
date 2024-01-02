package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.rules.Rule;
import lombok.Builder;

import java.util.List;
import java.util.Map;


public class ComplexDecision extends DecisionNode implements Node {


    private Map<String, String> options;
    private Rule rule;

    @Builder
    public ComplexDecision(String id, List<String> dataRefPath, Map<String, String> options, Rule rule) {
        super(id, dataRefPath);
        this.options = options;
        this.rule = rule;
    }

    @Override
    public String nextNode(String data) {
        return rule.getNextNode(data, options);
    }
}
