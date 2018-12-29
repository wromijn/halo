package com.github.wromijn.simplehal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JsonSerialize(using = HalSerializer.class)
public class HalRepresentation {

    Map<String, Object> properties = new TreeMap<>();

    Map<String, Object> links = new TreeMap<>();

    Map<String, Object> embedded = new TreeMap<>();

    public HalRepresentation addLink(String rel, String uri) {
        links.put(rel, new Link(uri));
        return this;
    }

    public HalRepresentation addBoolean(String name, Boolean b) {
        properties.put(name, b);
        return this;
    }

    public HalRepresentation addInteger(String name, Integer value) {
        properties.put(name, value);
        return this;
    }

    public HalRepresentation addNumber(String name, Double value) {
        properties.put(name, value);
        return this;
    }

    public HalRepresentation addNumber(String name, Float value) {
        properties.put(name, value);
        return this;
    }

    public HalRepresentation addString(String name, String value) {
        properties.put(name, value);
        return this;
    }

    public HalRepresentation addInline(String name, HalRepresentation representation) {
        properties.put(name, representation.properties);
        return this;
    }

    public HalRepresentation addEmbedded(String rel, HalRepresentation representation) {
        embedded.put(rel, representation);
        return this;
    }

    public HalRepresentation addEmbedded(String rel, Iterable<? extends HalRepresentation> representations) {
        addEmbedded(rel, StreamSupport.stream(representations.spliterator(), false));
        return this;
    }

    public HalRepresentation addEmbedded(String rel, Stream<? extends HalRepresentation> representations) {
        embedded.put(rel, representations.collect(Collectors.toList()));
        return this;
    }
}
