package com.github.wromijn.halo.postprocessors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wromijn.halo.Representation;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RemoveDuplicateCuriesTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testItDoesNotRemoveDuplicateCuriesInSiblings() {
        Representation representation = getRepresentation()
                .addEmbedded("ns:object1", getRepresentation()
                        .addCurie("ns", "/test"))
                .addEmbedded("ns:object2",getRepresentation()
                        .addCurie("ns", "/test"));
        JsonNode json = objectMapper.valueToTree(representation);
        assertThat(json.get("_embedded").get("ns:object1").get("_links").get("curies").size(), is(1));
        assertThat(json.get("_embedded").get("ns:object2").get("_links").get("curies").size(), is(1));
    }

    @Test
    public void testItDoesRemoveDuplicateCuriesInChildren() {
        Representation representation = getRepresentation()
                .addCurie("ns2", "/test")
                .addEmbedded("ns:object", getRepresentation()
                        .addCurie("ns1", "/test")
                        .addCurie("ns2", "/test")
                        .addCurie("ns3", "/test")
                );
        JsonNode json = objectMapper.valueToTree(representation);
        assertThat(json.get("_links").get("curies").size(), is(1));
        assertThat(json.get("_embedded").get("ns:object").get("_links").get("curies").size(), is(2));
        assertThat(json.get("_embedded").get("ns:object").get("_links").get("curies").get(0).get("name").asText(), is("ns1"));
        assertThat(json.get("_embedded").get("ns:object").get("_links").get("curies").get(1).get("name").asText(), is("ns3"));
    }

    @Test
    public void testItRemovesEmptyLinkBlocks() {
        Representation representation = getRepresentation()
                .addCurie("ns", "/test")
                .addEmbedded("ns:object", getRepresentation()
                        .addCurie("ns", "/test")
                        .addEmbedded("ns:object", getRepresentation()
                                .addCurie("ns", "/test")));
        JsonNode json = objectMapper.valueToTree(representation);
        assertThat(json.get("_embedded").get("ns:object").get("_links"), is(nullValue()));
        assertThat(json.get("_embedded").get("ns:object").get("_embedded").get("ns:object").get("_links"), is(nullValue()));
    }

    @Test
    public void testItDoesNotRemoveCuriesInChildrenWithDifferentHref() {
        Representation representation = getRepresentation()
                .addCurie("ns", "/test1")
                .addEmbedded("ns:object", getRepresentation()
                        .addCurie("ns", "/test2")
                        .addEmbedded("ns:object", getRepresentation()
                                .addCurie("ns", "/test1")));
        JsonNode json = objectMapper.valueToTree(representation);
        assertThat(json.get("_links").get("curies").size(), is(1));
        assertThat(json.get("_embedded").get("ns:object").get("_links").get("curies").size(), is(1));
        assertThat(json.get("_embedded").get("ns:object").get("_embedded").get("ns:object").get("_links").get("curies").size(), is(1));
    }

    private Representation getRepresentation() {
        return new Representation().addPostProcessor(PostProcessors::removeDuplicateCuries);
    }
}
