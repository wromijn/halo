package com.github.wromijn.halo.postprocessors;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface PostProcessor {
    void process(JsonNode jsonNode);
}
