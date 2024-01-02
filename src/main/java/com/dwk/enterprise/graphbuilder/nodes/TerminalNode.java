package com.dwk.enterprise.graphbuilder.nodes;

import lombok.Builder;

import java.util.List;

public class TerminalNode extends DecisionNode implements Node {

    private String exitRef;

    @Builder
    public TerminalNode(String id, List<String> dataRefPath, String exitRef) {
        super(id, dataRefPath);
        this.exitRef = exitRef;
    }

    @Override
    public String nextNode(String data) {
        return exitRef;
    }
}
