package com.github.wromijn.simplehal;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.stream.Stream;

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
    public void testLinks() {
        HalRepresentation representation = new HalRepresentation()
                .addLink("simple_link", "http://www.example.com/single")
                .addLink("link_object", new Link("http://www.example.com/object"))
                .addLinks("link_list", Collections.singleton(new Link("http://www.example.com/list")))
                .addLinks("link_stream", Stream.of(new Link("http://www.example.com/stream")));
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
                .addEmbedded("embedded_stream", Stream.of(createObjectRepresentation()))
                .addEmbedded("embedded_list", Collections.singleton(createObjectRepresentation()));
        schemaUtils.assertSerializationMatchesSchema(representation, "/schemas/hal_representation_test/embedded_test.json");
    }

    private HalRepresentation createObjectRepresentation() {
        return new HalRepresentation()
                .addString("inline_property", "inline_property")
                .addLink("self", "http://www.example.com")
                .addEmbedded("embedded", new HalRepresentation().addString("key", "value"));
    }
}
