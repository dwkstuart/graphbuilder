package com.dwk.enterprise.graphbuilder.data;

import lombok.Data;

import java.util.Map;

@Data
public class NodeDto {

    boolean decisionNode;
    Map<String, String> options;
    private String id;
    private String next;
    private String ruleName;
}
