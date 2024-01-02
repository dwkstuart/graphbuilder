package com.dwk.enterprise.graphbuilder.rules;

import com.dwk.enterprise.graphbuilder.util.JsonProcessorUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RuleOne implements Rule {


    public String getNextNode(String data, Map<String, String> options) {
        int age = (int) JsonProcessorUtil.getValueAtLocation(data, List.of("Customer", "age")).orElseThrow();
        return age > 20 ? options.get("a") : options.get("b");
    }
}
