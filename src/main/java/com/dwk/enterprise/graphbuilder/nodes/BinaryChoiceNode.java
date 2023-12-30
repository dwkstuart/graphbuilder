package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.Map;

@Builder
public class BinaryChoiceNode extends DecisionNode implements Node {

    private String id;
    @Builder.Default
    private boolean isBinaryChoiceRule = true;
    private Map<String, String> options;
    private Object trueValue;
    private String dataType;
    private String fieldName;

    @Override
    public String nextNode(Map<String, RulesData> data) {
        RulesData rulesData = data.get(dataType);
        boolean response = checkValue(rulesData);
        return response ? options.get(BoolEnum.TRUE.name()) : options.get(BoolEnum.FALSE.name());
    }

    private boolean checkValue(RulesData data) {
        ObjectMapper mapper = new ObjectMapper();
        var map = mapper.convertValue(data, Map.class);
        Object o = String.valueOf(map.get(fieldName));
        return o.equals(trueValue);
    }
}
