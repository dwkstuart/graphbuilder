package com.dwk.enterprise.graphbuilder.data;

import lombok.Data;

import java.util.Map;

@Data
public class NodeDto {

    NodeType nodeType = NodeType.STANDARD_NODE;
    Map<String, String> options;
    private String id;
    private String next;
    private String ruleName;
    private String trueValue;
    private String dataType;
    private String fieldName;
}
