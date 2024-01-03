package com.dwk.enterprise.graphbuilder.nodes;

import com.dwk.enterprise.graphbuilder.data.Operand;
import com.dwk.enterprise.graphbuilder.util.JsonProcessorUtil;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class BinaryChoiceNode extends DecisionNode implements Node {


    private final Map<String, String> options;
    private final Object comparator;
    private final Operand operand;


    @Builder
    public BinaryChoiceNode(List<String> dataRefPath, String id, Map<String, String> options, Object comparator, Operand operand) {
        super(id, dataRefPath);
        this.options = options;
        this.comparator = comparator;
        this.operand = operand;
    }

    @Override
    public String getNextNodeId(String data) {
        Optional<Object> valueAtLocation = JsonProcessorUtil.getValueAtLocation(data, super.getDataRefPath());
        var value = valueAtLocation.orElseThrow();

        boolean response = evaluateValueAgainstComparator(value);
        boolean b = options.containsKey(BoolEnum.TRUE.name()) && options.containsKey(BoolEnum.FALSE.name());
        if (!b) {
            throw new RuntimeException("no binary options provided");
        }
        return response ? options.get(BoolEnum.TRUE.name()) : options.get(BoolEnum.FALSE.name());
    }

    private boolean evaluateValueAgainstComparator(Object value) {

        return switch (operand) {

            case EQUALS -> value.equals(comparator);
            case GREATER_THAN -> (Integer) value > (Integer) comparator;
            case GREATER_OR_EQUALS -> (Integer) value >= (Integer) comparator;
            case LESS_THAN -> (Integer) value < (Integer) comparator;
            case NOT_EQUALS -> !value.equals(comparator);
        };


    }


}
