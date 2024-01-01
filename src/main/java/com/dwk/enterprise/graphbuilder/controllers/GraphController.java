package com.dwk.enterprise.graphbuilder.controllers;

import com.dwk.enterprise.graphbuilder.data.GraphDto;
import com.dwk.enterprise.graphbuilder.util.GraphLoader;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
public class GraphController {

    private final GraphLoader graphLoader;

    @PostMapping(value = "/graph/{graphName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addNewGraph(@PathVariable String graphName, @RequestBody GraphDto graphDto) {
        try {
            graphLoader.createGraph(graphName, new Gson().toJson(graphDto));
            return graphName;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/graphs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String existingGraphs() {
        return new Gson().toJson(graphLoader.getAllGraphs());
    }
}
