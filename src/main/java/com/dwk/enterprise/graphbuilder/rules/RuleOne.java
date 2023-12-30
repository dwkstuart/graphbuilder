package com.dwk.enterprise.graphbuilder.rules;

import com.dwk.enterprise.graphbuilder.interfaces.Customer;
import com.dwk.enterprise.graphbuilder.interfaces.RulesData;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleOne implements Rule {


    public String getNextNode(Map<String, RulesData> data, Map<String, String> options) {

        Customer customer = (Customer) data.get("Customer");
        int age = customer.getAge();
        return age > 20 ? options.get("a") : options.get("b");
    }
}
