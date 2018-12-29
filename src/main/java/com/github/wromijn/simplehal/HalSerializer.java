package com.github.wromijn.simplehal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

class HalSerializer extends JsonSerializer<HalRepresentation> {
    @Override
    public void serialize(HalRepresentation halRepresentation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.valueToTree(halRepresentation.properties);
        if (! halRepresentation.links.isEmpty()) {
            rootNode.set("_links", objectMapper.valueToTree(halRepresentation.links));
        }
        if (! halRepresentation.embedded.isEmpty()) {
            rootNode.set("_embedded", objectMapper.valueToTree(halRepresentation.embedded));
        }
        jsonGenerator.writeObject(rootNode);
    }
}
