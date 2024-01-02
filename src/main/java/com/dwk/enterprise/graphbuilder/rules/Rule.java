package com.dwk.enterprise.graphbuilder.rules;



import java.util.Map;

public interface Rule {

    String getNextNode(String data, Map<String, String> options);
}
