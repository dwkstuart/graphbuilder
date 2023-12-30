package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.nodes.Node;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StandardNode  implements Node {

    private String id;
    private String nextNode;

    @Override
    public String nextNode(Map<String, Object> data) {
        return nextNode;
    }
}
