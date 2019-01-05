package com.github.wromijn.simplehal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HalRepresentationTest {

    private SchemaUtils schemaUtils = new SchemaUtils();

    @Test
    public void testProperties() {
        HalRepresentation representation = new HalRepresentation()
                .addBoolean("boolean_true", true)
                .addBoolean("boolean_false", false)
                .addBoolean("boolean_null", null)
                .addInteger("integer_byte", Byte.MAX_VALUE)
                .addInteger("integer_value", 123)
                .addInteger("integer_long", 123L)
                .addInteger("integer_float", 1.5)
                .addInteger("integer_double", 2.5F)
                .addInteger("integer_null", null)
                .addNumber("number_float", 123.456F)
                .addNumber("number_double", 123.456)
                .addNumber("number_integer", 1)
                .addNumber("number_null", null)
                .addString("string_value", "hello")
                .addString("string_null", null);
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/property_test.json");
    }

    @Test
    public void testLists() {
        HalRepresentation representation = new HalRepresentation()
                .addBooleanList("boolean_list", Collections.singletonList(true))
                .addBooleanList("boolean_stream", Stream.of(false))
                .addIntegerList("integer_list", Collections.singletonList(1))
                .addIntegerList("integer_stream", Stream.of(2))
                .addNumberList("number_list", Collections.singletonList(3))
                .addNumberList("number_stream", Stream.of(4))
                .addStringList("string_list", Collections.singletonList("one"))
                .addStringList("string_stream", Stream.of("two"))
                .addInlineList("inline_list", Collections.singletonList(createObjectRepresentation()))
                .addInlineList("inline_stream", Stream.of(createObjectRepresentation()));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/list_test.json");
    }

    @Test
    public void testLinks() {
        HalRepresentation representation = new HalRepresentation()
                .addLink("simple_link", "http://www.example.com/single")
                .addLink("link_object", new Link("http://www.example.com/object"))
                .addLinkList("link_list", Collections.singleton(new Link("http://www.example.com/list")))
                .addLinkList("link_stream", Stream.of(new Link("http://www.example.com/stream")));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/link_test.json");
    }

    @Test
    public void testInline() {
        HalRepresentation representation = new HalRepresentation()
                .addInline("inline_object", createObjectRepresentation());
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/inline_test.json");
    }

    @Test
    public void testEmbedded() {
        HalRepresentation representation = new HalRepresentation()
                .addEmbedded("embedded_object", createObjectRepresentation())
                .addEmbeddedList("embedded_stream", Stream.of(createObjectRepresentation()))
                .addEmbeddedList("embedded_list", Collections.singleton(createObjectRepresentation()));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/embedded_test.json");
    }

    @Test
    public void testFromJsonNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("name", "value");
        node.put("number", 1);
        node.put("boolean", false);
        HalRepresentation representation = HalRepresentation.ofJsonNode(node);
        assertThat(representation.properties.get("name"), is("value"));
        assertThat(representation.properties.get("number"), is(1));
        assertThat(representation.properties.get("boolean"), is(false));
    }

    @Test
    public void testFromJsonNodeWithError() {
        JsonNode node = new ObjectMapper().createArrayNode();
        assertThrows(IllegalArgumentException.class, () -> HalRepresentation.ofJsonNode(node));
    }

    @Test
    public void testRemoveProperty() {
        HalRepresentation representation = new HalRepresentation()
                .addInteger("integer1", 1)
                .addInteger("integer2", 2);
        assert representation.properties.size() == 2;
        representation.removeProperty("integer1");
        assertThat(representation.properties.size(), is(1));
        assertThat(representation.properties.get("integer2"), is(2));
    }

    @Test
    public void testRemoveLink() {
        HalRepresentation representation = new HalRepresentation()
                .addLink("link1", "http://www.example.com/1")
                .addLink("link2", "http://www.example.com/2");
        assert representation._links.size() == 2;
        representation.removeLink("link1");
        assertThat(representation._links.size(), is(1));
        assertThat(representation._links.get("link2"), is(new Link("http://www.example.com/2")));
    }

    @Test
    public void testRemoveEmbedded() {
        HalRepresentation object1 = createObjectRepresentation();
        HalRepresentation object2 = createObjectRepresentation();

        HalRepresentation representation = new HalRepresentation()
                .addEmbedded("object1", object1)
                .addEmbedded("object2", object2);
        assert representation._embedded.size() == 2;
        representation.removeEmbedded("object1");
        assertThat(representation._embedded.size(), is(1));
        assertThat(representation._embedded.get("object2"), is(object2));
    }

    private HalRepresentation createObjectRepresentation() {
        return new HalRepresentation()
                .addString("inline_property", "inline_property")
                .addLink("self", "http://www.example.com")
                .addEmbedded("embedded", new HalRepresentation().addString("key", "value"));
    }
}
