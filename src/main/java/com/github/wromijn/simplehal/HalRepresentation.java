package com.github.wromijn.simplehal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JsonSerialize(using = HalSerializer.class)
public final class HalRepresentation {

    Map<String, Object> properties = new TreeMap<>();

    Map<String, Object> _links = new TreeMap<>();

    Map<String, Object> _embedded = new TreeMap<>();

    public static HalRepresentation ofJsonNode(JsonNode jsonNode) {
        HalRepresentation representation = new HalRepresentation();
        try {
            representation.properties.putAll(new ObjectMapper().treeToValue(jsonNode, Map.class));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return representation;
    }

    // --------------------------------------------------------------

    public HalRepresentation addBoolean(String name, Boolean bool) {
        properties.put(name, bool);
        return this;
    }

    public HalRepresentation addBooleanList(String name, Iterable<Boolean> booleans) {
        addBooleanList(name, StreamSupport.stream(booleans.spliterator(), false));
        return this;
    }

    public HalRepresentation addBooleanList(String name, Stream<Boolean> booleans) {
        properties.put(name, booleans.toArray());
        return this;
    }

    public HalRepresentation addInteger(String name, Number number) {
        properties.put(name, normalizeInteger(number));
        return this;
    }

    public HalRepresentation addIntegerList(String name, Iterable<Number> numbers) {
        addIntegerList(name, StreamSupport.stream(numbers.spliterator(), false));
        return this;
    }

    public HalRepresentation addIntegerList(String name, Stream<Number> numbers) {
        properties.put(name, numbers.toArray());
        return this;
    }

    public HalRepresentation addNumber(String name, Number value) {
        properties.put(name, normalizeNumber(value));
        return this;
    }

    public HalRepresentation addNumberList(String name, Iterable<Number> numbers) {
        addNumberList(name, StreamSupport.stream(numbers.spliterator(), false));
        return this;
    }

    public HalRepresentation addNumberList(String name, Stream<Number> numbers) {
        properties.put(name, numbers.map(this::normalizeNumber).toArray());
        return this;
    }

    public HalRepresentation addString(String name, String value) {
        properties.put(name, value);
        return this;
    }

    public HalRepresentation addStringList(String name, Iterable<String> strings) {
        properties.put(name, strings);
        return this;
    }

    public HalRepresentation addStringList(String name, Stream<String> strings) {
        properties.put(name, strings.toArray());
        return this;
    }

    private Number normalizeNumber(Number n) {
        if (n == null || n instanceof Double || n instanceof Float) {
            return n;
        } else {
            return n.floatValue();
        }
    }

    private Number normalizeInteger(Number n) {
        if (n instanceof Double || n instanceof Float) {
            return n.longValue();
        } else {
            return n;
        }
    }

    // --------------------------------------------------------------

    public HalRepresentation addInline(String name, HalRepresentation representation) {
        properties.put(name, representation.properties);
        return this;
    }

    public HalRepresentation addInlineList(String name, Iterable<? extends HalRepresentation> representations) {
        addInlineList(name, StreamSupport.stream(representations.spliterator(), false));
        return this;
    }

    public HalRepresentation addInlineList(String name, Stream<? extends HalRepresentation> representations) {
        properties.put(name, representations.toArray());
        return this;
    }

    // --------------------------------------------------------------

    public HalRepresentation addLink(String rel, String uri) {
        _links.put(rel, new Link(uri));
        return this;
    }

    public HalRepresentation addLink(String rel, Link link) {
        _links.put(rel, link);
        return this;
    }

    public HalRepresentation addLinkList(String rel, Iterable<? extends Link> links) {
        addLinkList(rel, StreamSupport.stream(links.spliterator(), false));
        return this;
    }

    public HalRepresentation addLinkList(String rel, Stream<? extends Link> links) {
        _links.put(rel, links.toArray());
        return this;
    }

    // --------------------------------------------------------------

    public HalRepresentation addEmbedded(String rel, HalRepresentation representation) {
        _embedded.put(rel, representation);
        return this;
    }

    public HalRepresentation addEmbeddedList(String rel, Iterable<? extends HalRepresentation> representations) {
        addEmbeddedList(rel, StreamSupport.stream(representations.spliterator(), false));
        return this;
    }

    public HalRepresentation addEmbeddedList(String rel, Stream<? extends HalRepresentation> representations) {
        _embedded.put(rel, representations.toArray());
        return this;
    }

    // --------------------------------------------------------------

    @Deprecated // forRemoval=false
    public HalRepresentation removeProperty(String name) {
        properties.remove(name);
        return this;
    }

    @Deprecated // forRemoval=false
    public HalRepresentation removeLink(String rel) {
        _links.remove(rel);
        return this;
    }

    @Deprecated // forRemoval=false
    public HalRepresentation removeEmbedded(String rel) {
        _embedded.remove(rel);
        return this;
    }
}
