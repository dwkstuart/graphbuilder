package com.dwk.enterprise.graphbuilder.data;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class NodeDto {

    NodeType nodeType = NodeType.STANDARD_NODE;
    Map<String, String> options;
    private String id;
    private String next;
    private String ruleName;
    private Integer intValueToCompare;
    private String stringValueToCompare;
    private Double doubleValueToCompare;
    private List<String> dataRefPath;
    private Operand operand;
    private String exitRef;
}
