package com.dwk.enterprise.graphbuilder.rules;


import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleOne implements Rule {


    public String getNextNode(String data, Map<String, String> options) {


        int age = 19;
        return age > 20 ? options.get("a") : options.get("b");
    }
}
