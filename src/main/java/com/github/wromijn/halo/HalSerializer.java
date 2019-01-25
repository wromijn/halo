package com.github.wromijn.halo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

class HalSerializer extends JsonSerializer<Representation> {
    @Override
    public void serialize(Representation representation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.valueToTree(representation.properties);
        if (! representation._links.isEmpty()) {
            rootNode.set("_links", objectMapper.valueToTree(representation._links));
        }
        if (! representation._embedded.isEmpty()) {
            rootNode.set("_embedded", objectMapper.valueToTree(representation._embedded));
        }
        jsonGenerator.writeObject(rootNode);
    }
}
