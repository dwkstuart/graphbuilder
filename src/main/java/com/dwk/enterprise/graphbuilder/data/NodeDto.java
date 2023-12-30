package com.dwk.enterprise.graphbuilder.data;

import lombok.Data;

import java.util.Map;

@Data
public class NodeDto {

    private String id;
    private String next;
    boolean decisionNode;
    private String ruleName;
    Map<String, String> options;
}
