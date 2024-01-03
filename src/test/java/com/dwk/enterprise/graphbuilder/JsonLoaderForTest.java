package com.dwk.enterprise.graphbuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonLoaderForTest {

  private static String getJsonString(String filename) throws IOException {
    try (InputStream resource = JsonLoaderForTest.class.getClassLoader().getResourceAsStream(filename)) {
      try (BufferedReader reader =
                   new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource)))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    }
  }

  public static String getGraphJsonFromResourcesFolder(String graphName) {
    try {
      return getJsonString(graphName + ".json");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
