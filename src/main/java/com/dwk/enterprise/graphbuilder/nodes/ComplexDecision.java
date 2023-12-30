package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
import com.dwk.enterprise.graphbuilder.rules.Rule;
import lombok.Builder;

import java.util.Map;


@Builder
public class ComplexDecision extends DecisionNode implements Node {

    private String id;
    private Map<String, String> options;
    private Rule rule;

    @Override
    public String nextNode(Map<String, RulesData> data) {
        return rule.getNextNode(data, options);
    }
}
