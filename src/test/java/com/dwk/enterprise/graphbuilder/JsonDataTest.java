package com.dwk.enterprise.graphbuilder;


import com.dwk.enterprise.graphbuilder.util.JsonProcessorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonDataTest {

    String testJson = """
            {
                "Customer": {
                    "firstName": "Bob",
                    "lastName": "Dole",
                    "age": 90,
                    "dataType": "Customer",
                    "addresses": [{"line1": "test"},{"line1": "test2"} ]
                }
            }
            """;

    @Test
    void setTestJson() {
//    JsonProcessorUtil processorUtil = new JsonProcessorUtil();
        Optional<?> valueAtLocation = JsonProcessorUtil.getValueAtLocation(testJson, List.of("Customer", "age"));
        Assertions.assertEquals(90, valueAtLocation.orElseThrow());
    }

    @Test
    void arrayTestJson() {
//        JsonProcessorUtil processorUtil = new JsonProcessorUtil();
        Optional<Object> valueAtLocation = JsonProcessorUtil.getValueAtLocation(testJson, List.of("Customer", "addresses"));
        List<Object> addressList = (List<Object>) valueAtLocation.get();
        Assertions.assertEquals(2, addressList.size());
    }

    @Test
    void mapTestJson() {
//        JsonProcessorUtil processorUtil = new JsonProcessorUtil();
        Optional<Object> valueAtLocation = JsonProcessorUtil.getValueAtLocation(testJson, List.of("Customer"));
        Map addressList = (Map) valueAtLocation.get();
        List addresses = (List) addressList.get("addresses");
        Assertions.assertEquals(2, addresses.size());
    }

}
