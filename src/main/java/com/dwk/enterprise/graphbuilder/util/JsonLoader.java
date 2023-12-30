package com.dwk.enterprise.graphbuilder.util;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.google.gson.Gson;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class JsonLoader {

    public static String getJsonString(String filename) throws IOException {
        InputStream resource = new ClassPathResource(
                filename).getInputStream();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    public static String getGraphJson(String graphName){
        try {
            return JsonLoader.getJsonString("flowgraphs/" + graphName + ".json");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
