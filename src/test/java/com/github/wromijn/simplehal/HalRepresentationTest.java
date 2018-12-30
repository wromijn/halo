package com.github.wromijn.simplehal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class HalRepresentationTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testProperties() throws Exception {
        HalRepresentation representation = new HalRepresentation()
                .addBoolean("boolean_true", true)
                .addBoolean("boolean_false", false)
                .addBoolean("boolean_null", null)
                .addInteger("integer_byte", Byte.MAX_VALUE)
                .addInteger("integer_value", 123)
                .addInteger("integer_long", 123L)
                .addInteger("integer_float", 1.5)
                .addInteger("integer_null", null)
                .addNumber("number_float", 123.456F)
                .addNumber("number_double", 123.456)
                .addNumber("number_integer", 1)
                .addNumber("number_null", null)
                .addString("string_value", "hello")
                .addString("string_null", null);
        matchAgainstSchema(objectMapper.writeValueAsString(representation), "/schemas/property_test.json");
    }

    @Test
    public void testLinks() throws Exception {
        HalRepresentation representation = new HalRepresentation()
                .addLink("simple_link", "http://www.example.com/single")
                .addLink("link_object", new Link("http://www.example.com/object"))
                .addLinks("link_list", Collections.singleton(new Link("http://www.example.com/list")))
                .addLinks("link_stream", Stream.of(new Link("http://www.example.com/stream")));
        matchAgainstSchema(objectMapper.writeValueAsString(representation), "/schemas/link_test.json");
    }

    @Test
    public void testInline() throws Exception {
        HalRepresentation representation = new HalRepresentation()
                .addInline("inline_object", createObjectRepresentation());
        matchAgainstSchema(objectMapper.writeValueAsString(representation), "/schemas/inline_test.json");
    }

    @Test
    public void testEmbedded() throws Exception {
        HalRepresentation representation = new HalRepresentation()
                .addEmbedded("embedded_object", createObjectRepresentation())
                .addEmbedded("embedded_stream", Stream.of(createObjectRepresentation()))
                .addEmbedded("embedded_list", Collections.singleton(createObjectRepresentation()));
        matchAgainstSchema(objectMapper.writeValueAsString(representation), "/schemas/embedded_test.json");
    }

    private void matchAgainstSchema(String response, String schemaPath) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(schemaPath)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(new JSONObject(response)); // throws a ValidationException if this object is invalid
        } catch (ValidationException e) {
            getValidationErrors(e).forEach(System.out::println);
            throw (e);
        }
    }

    private List<String> getValidationErrors(ValidationException e) {
        List<String> result = new ArrayList<>();
        result.add(e.getMessage());
        result.addAll(e.getCausingExceptions().stream().map(this::getValidationErrors).flatMap(List::stream).collect(Collectors.toList()));
        return result;
    }

    private HalRepresentation createObjectRepresentation() {
        return new HalRepresentation()
                .addString("inline_property", "inline_property")
                .addLink("self", "http://www.example.com")
                .addEmbedded("embedded", new HalRepresentation().addString("key", "value"));
    }
}
