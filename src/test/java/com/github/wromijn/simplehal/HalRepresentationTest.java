package com.github.wromijn.simplehal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class HalRepresentationTest {
    public void testProperties() {
        HalRepresentation representation = new HalRepresentation();
        representation.addBoolean("boolean_value", true);
        JsonNode result = new ObjectMapper().valueToTree(representation);
        assertThat(result.get("boolean_value").booleanValue(), is(true));

        representation.addBoolean("boolean_value", false);
        result = new ObjectMapper().valueToTree(representation);
        assertThat(result.get("boolean_value").booleanValue(), is(false));
    }
}
