package com.dwk.enterprise.graphbuilder.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonLoader {

  public static String getJsonString(String filename) throws IOException {
    try (InputStream resource = JsonLoader.class.getClassLoader().getResourceAsStream(filename)) {
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource)))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    }
  }

  public static String getGraphJsonFromResourcesFolder(String graphName) {
    try {
      return JsonLoader.getJsonString("flowgraphs/" + graphName + ".json");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
