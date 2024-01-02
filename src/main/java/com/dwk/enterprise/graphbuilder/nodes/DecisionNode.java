package com.dwk.enterprise.graphbuilder.nodes;

import lombok.AllArgsConstructor;

import java.util.List;

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
