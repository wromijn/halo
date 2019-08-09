package com.github.wromijn.halo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JsonSerialize(using = HalSerializer.class)
public final class Representation {

    Map<String, Object> properties = new TreeMap<>();

    Map<String, Object> _links = new TreeMap<>();

    Map<String, Object> _embedded = new TreeMap<>();

    public static Representation fromObject(Object object, ObjectMapper objectMapper) {
        Representation representation = new Representation();
        try {
            JsonNode jsonNode = objectMapper.valueToTree(object);
            representation.properties.putAll(objectMapper.treeToValue(jsonNode, Map.class));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return representation;
    }

    public static Representation fromObject(Object o) {
        return fromObject(o, new ObjectMapper());
    }

    // --------------------------------------------------------------

    public Representation addBoolean(String name, Boolean bool) {
        properties.put(name, bool);
        return this;
    }

    public Representation addInteger(String name, Number number) {
        properties.put(name, normalizeInteger(number));
        return this;
    }

    public Representation addNumber(String name, Number value) {
        properties.put(name, normalizeNumber(value));
        return this;
    }

    public Representation addString(String name, String value) {
        properties.put(name, value);
        return this;
    }

    public Representation addOther(String name, Object o) {
        properties.put(name, o);
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

    public Representation include(Representation representation) {
        this.properties.putAll(representation.properties);
        this._links.putAll(representation._links);
        this._embedded.putAll(representation._embedded);
        return this;
    }
    
    // --------------------------------------------------------------

    public Representation addInline(String name, Representation representation) {
        properties.put(name, representation.properties);
        return this;
    }

    public Representation addInlineList(String name, Iterable<? extends Representation> representations) {
        addInlineList(name, StreamSupport.stream(representations.spliterator(), false));
        return this;
    }

    public Representation addInlineList(String name, Stream<? extends Representation> representations) {
        properties.put(name, representations.map(r -> r.properties).toArray());
        return this;
    }

    // --------------------------------------------------------------

    public Representation addLink(String rel, String uri) {
        _links.put(rel, new Link(uri));
        return this;
    }

    public Representation addLink(String rel, Link link) {
        _links.put(rel, link);
        return this;
    }

    public Representation addLinkList(String rel, Iterable<? extends Link> links) {
        addLinkList(rel, StreamSupport.stream(links.spliterator(), false));
        return this;
    }

    public Representation addLinkList(String rel, Stream<? extends Link> links) {
        _links.put(rel, links.toArray());
        return this;
    }

    // --------------------------------------------------------------

    public Representation addCurie(String curie, String href) {
        Link[] curies;
        Object curiesObject = _links.get("curies");
        if (curiesObject instanceof Link[]) {
            curies = (Link[]) curiesObject;
            curies = Arrays.copyOf(curies, curies.length + 1);
        } else {
            curies = new Link[1];
        }
        curies[curies.length-1] = new Link(href).setName(curie);
        _links.put("curies", curies);
        return this;
    }

    // --------------------------------------------------------------

    public Representation addEmbedded(String rel, Representation representation) {
        _embedded.put(rel, representation);
        return this;
    }

    public Representation addEmbeddedList(String rel, Iterable<? extends Representation> representations) {
        addEmbeddedList(rel, StreamSupport.stream(representations.spliterator(), false));
        return this;
    }

    public Representation addEmbeddedList(String rel, Stream<? extends Representation> representations) {
        _embedded.put(rel, representations.toArray());
        return this;
    }
}
