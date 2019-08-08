package com.github.wromijn.halo.postprocessors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostProcessors {

// http://www.iana.org/assignments/link-relations/link-relations.xhtml
    private static final Map<String, String> DEFAULT_TITLES = new HashMap<String, String>() {{
        put("profile", "Documentation for this resource");
        put("self", "URI of this resource");
        put("collection", "Back to the collection");
        put("create-form", "Form to create a new resource in this collection");
        put("first", "First page");
        put("last", "Last page");
        put("next", "Next page");
        put("previous", "Previous page");
        put("prev", "Previous page");
    }};

    public static void addDefaultTitles(JsonNode jsonNode) {
        jsonNode.path("_links").fields().forEachRemaining(entry -> {
            if (entry.getValue().path("title").isMissingNode()) {
                String defaultTitle = DEFAULT_TITLES.get(entry.getKey());
                if (defaultTitle != null) {
                    ((ObjectNode) entry.getValue()).put("title", defaultTitle);
                }
            }
        });
    }

    public static void removeDuplicateCuries(JsonNode jsonNode) {
        Map<String, JsonNode> myCuries = getIndexedCuries(jsonNode);
        collectNodes(jsonNode.path("_embedded")).forEach(embeddedRepresentationNode ->
                getIndexedCuries(embeddedRepresentationNode).entrySet().stream()
                        .filter(entry -> entry.getValue().equals(myCuries.get(entry.getKey())))
                        .forEach(entry -> removeCurie(embeddedRepresentationNode, entry.getKey()))
        );
    }

    private static List<JsonNode> collectNodes(JsonNode node) {
        List<JsonNode> items = new ArrayList<>();
        node.forEach(value -> {
                    if (value instanceof ObjectNode) {
                        items.add(value);
                    } else if (value instanceof ArrayNode) {
                        value.forEach(items::add);
                    }
                }
        );
        return items;
    }

    private static Map<String, JsonNode> getIndexedCuries(JsonNode representation) {
        Map<String, JsonNode> result = new HashMap<>();
        representation.at("/_links/curies").forEach(curie -> result.put(curie.get("name").asText(), curie));
        return result;
    }

    private static void removeCurie(JsonNode representation, String name) {
        ArrayNode curies = (ArrayNode) representation.at("/_links/curies");
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
            ((ObjectNode) representation).remove("_links");
        }
    }
}
