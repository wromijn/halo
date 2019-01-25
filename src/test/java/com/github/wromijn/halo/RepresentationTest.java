package com.github.wromijn.halo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepresentationTest {

    private SchemaUtils schemaUtils = new SchemaUtils();

    @Test
    public void testProperties() {
        Representation representation = new Representation()
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
        Representation representation = new Representation()
                .addOther("boolean_list", Collections.singletonList(true))
                .addOther("integer_list", Collections.singletonList(1))
                .addOther("number_list", Collections.singletonList(3F))
                .addOther("string_list", Collections.singletonList("one"));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/array_test.json");
    }

    @Test
    public void testLinks() {
        Representation representation = new Representation()
                .addLink("simple_link", "http://www.example.com/single")
                .addLink("link_object", new Link("http://www.example.com/object"))
                .addLinkList("link_list", Collections.singleton(new Link("http://www.example.com/list")))
                .addLinkList("link_stream", Stream.of(new Link("http://www.example.com/stream")));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/link_test.json");
    }

    @Test
    public void testInline() {
        Representation representation = new Representation()
                .addInline("inline_object", createObjectRepresentation())
                .addInlineList("inline_list", Collections.singletonList(createObjectRepresentation()))
                .addInlineList("inline_stream", Stream.of(createObjectRepresentation()));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/inline_test.json");
    }

    @Test
    public void testEmbedded() {
        Representation representation = new Representation()
                .addEmbedded("embedded_object", createObjectRepresentation())
                .addEmbeddedList("embedded_stream", Stream.of(createObjectRepresentation()))
                .addEmbeddedList("embedded_list", Collections.singleton(createObjectRepresentation()));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/embedded_test.json");
    }

    @Test
    public void testFromObject() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("name", "value");
        node.put("number", 1);
        node.put("boolean", false);
        Representation representation = Representation.fromObject(node);
        assertThat(representation.properties.get("name"), is("value"));
        assertThat(representation.properties.get("number"), is(1));
        assertThat(representation.properties.get("boolean"), is(false));
    }

    @Test
    public void testFromObjectWithError() {
        JsonNode node = new ObjectMapper().createArrayNode();
        assertThrows(IllegalArgumentException.class, () -> Representation.fromObject(node));
    }

    private Representation createObjectRepresentation() {
        return new Representation()
                .addString("inline_property", "inline_property")
                .addLink("self", "http://www.example.com")
                .addEmbedded("embedded", new Representation().addString("key", "value"));
    }
}
