package com.github.wromijn.halo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;

class HalSerializer extends JsonSerializer<Representation> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(Representation representation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectNode rootNode = objectMapper.valueToTree(representation.properties);
        if (!representation._links.isEmpty()) {
            rootNode.set("_links", objectMapper.valueToTree(representation._links));
        }
        if (!representation._embedded.isEmpty()) {
            rootNode.set("_embedded", objectMapper.valueToTree(representation._embedded));
        }
        removeDuplicateCuriesInEmbedded(rootNode);
        jsonGenerator.writeObject(rootNode);
    }

    private void removeDuplicateCuriesInEmbedded(ObjectNode objectNode) {
        removeDuplicateCuriesInEmbedded(objectNode, new HashMap<>());
    }

    private void removeDuplicateCuriesInEmbedded(ObjectNode representationNode, Map<String, ObjectNode> curieIndex) {
        embeddedRepresentationNodes(representationNode).forEach(embeddedRepresentationNode -> {
                    Map<String, ObjectNode> myCuries = new HashMap<>(curieIndex);
                    myCuries.putAll(getCuries(representationNode));
                    Map<String, ObjectNode> childCuries = getCuries(embeddedRepresentationNode);
                    childCuries.forEach((name, curieNode) -> {
                        if (curieNode.equals(myCuries.get(name))) {
                            removeCurie(embeddedRepresentationNode, name);
                        }
                    });
                    removeDuplicateCuriesInEmbedded(embeddedRepresentationNode, myCuries);
                }
        );
    }

    private List<ObjectNode> embeddedRepresentationNodes(ObjectNode objectNode) {
        List<ObjectNode> items = new ArrayList<>();
        if (objectNode.get("_embedded") instanceof ObjectNode) {
            objectNode.get("_embedded").fields().forEachRemaining(
                    entry -> {
                        if (entry.getValue() instanceof ObjectNode) {
                            items.add((ObjectNode) entry.getValue());
                        }
                        if (entry.getValue() instanceof ArrayNode) {
                            entry.getValue().forEach(
                                    child -> {
                                        items.add((ObjectNode) child);
                                    }
                            );
                        }
                    }
            );
        }
        return items;
    }

    private Map<String, ObjectNode> getCuries(JsonNode representation) {
        Map<String, ObjectNode> result = new HashMap<>();
        if (representation.get("_links") != null && representation.get("_links").get("curies") instanceof ArrayNode) {
            ArrayNode curies = (ArrayNode) representation.get("_links").get("curies");
            for (JsonNode curie : curies) {
                result.put(curie.get("name").asText(), (ObjectNode) curie);
            }
        }
        return result;
    }

    private void removeCurie(ObjectNode representation, String name) {
        ArrayNode curies = (ArrayNode) representation.get("_links").get("curies");
        for (int i = 0; i < curies.size(); i++) {
            if (curies.get(i).get("name").asText().equals(name)) {
                curies.remove(i);
                break;
            }
        }
        if (curies.size() == 0) {
            ((ObjectNode) representation.get("_links")).remove("curies");
        }
        if (representation.get("_links").size() == 0) {
            representation.remove("_links");
        }
    }
}
