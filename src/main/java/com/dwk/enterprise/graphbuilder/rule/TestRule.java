package com.dwk.enterprise.graphbuilder.rule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;

public class TestRule implements CustomRule {
    @Override
    public String getKeyForOptions(String data) {
        ObjectMapper mapper = new ObjectMapper();
        Customer customer = new Customer();
        try {
            Root customerWrapper = mapper.readValue(data, Root.class);
            customer = customerWrapper.getCustomer();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        ;
//        Optional<Object> valueAtLocation = JsonProcessorUtil.getValueAtLocation(data, List.of("Customer"));
//        var value = valueAtLocation.orElseThrow();
//
//        Customer customer = mapper.readValue(value, Customer.class);
        if (customer.firstName.length() == 3 && customer.age > 19) {
            return "a";
        }
        return "b";

    }

    @Data
    public static class Address {
        public String line1;
    }

    @Data
    public static class Customer {
        public String firstName;
        public String lastName;
        public int age;
        public String dataType;
        public ArrayList<Address> addresses;
    }

    @Data
    public static class Root {
        @JsonProperty("Customer")
        public Customer customer;

        public Customer getCustomer() {
            return customer;
        }
    }

}
