package com.dwk.enterprise.graphbuilder.rules;

import com.dwk.enterprise.graphbuilder.rules.Rule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleOne implements Rule {


    public String getNextNode(Map<String, Object> data, Map<String, String> options){
        Map<String, Object> customer = (Map<String, Object>) data.get("Customer");
        int age = (int) customer.get("age");
        return age > 20 ? options.get("a"): options.get("b");
    }
}
