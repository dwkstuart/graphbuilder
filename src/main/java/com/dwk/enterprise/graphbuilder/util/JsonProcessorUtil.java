package com.dwk.enterprise.graphbuilder.util;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.*;


public abstract class JsonProcessorUtil {

    public static Optional<Object> getValueAtLocation(String json, List<String> fieldRef) {
        ObjectMapper mapper = new ObjectMapper();
        String ref = StringUtils.collectionToDelimitedString(fieldRef, "/");
        JsonPointer jsonPointer = JsonPointer.compile("/" + ref);
        JsonNode node1;
        try {

            JsonNode node = mapper.readTree(json);
            node1 = node.at(jsonPointer);
            System.out.println(node1);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return switch (node1.getNodeType()) {
            case ARRAY -> Optional.of(getList(node1));
            case BINARY, NULL -> Optional.empty();
            case BOOLEAN -> Optional.of(node1.asBoolean());
            case MISSING -> Optional.empty();
            case NUMBER -> Optional.of(node1.numberValue());
            case OBJECT, POJO -> Optional.of(mapper.convertValue(node1, Map.class));
            case STRING -> Optional.of(node1.asText());
        };
    }

    private static List<Object> getList(JsonNode node1) {

        List<Object> list = new ArrayList<>();
        Iterator<JsonNode> it = node1.iterator();
        for (; it.hasNext(); ) {
            list.add(it.next());
        }

        return list;
    }


}
