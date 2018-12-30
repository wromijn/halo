package com.github.wromijn.simplehal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class HalRepresentationTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testProperties() throws Exception {
        HalRepresentation representation = new HalRepresentation()
                .addBoolean("boolean_true", true)
                .addBoolean("boolean_false", false)
                .addInteger("byte_value", Byte.MAX_VALUE)
                .addInteger("integer_value", 123)
                .addInteger("long_value", 123L)
                .addNumber("float_value", 123.456F)
                .addNumber("double_value", 123.456)
                .addString("string_value", "hello")
                .addString("char_value", 'h');
        matchAgainstSchema(objectMapper.writeValueAsString(representation), "/schemas/property_test.json");
    }

    private void matchAgainstSchema(String response, String schemaPath) throws Exception {
        String jsonSchemaUri = getClass().getResource(schemaPath).toString();
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(jsonSchemaUri);
        ProcessingReport report = schema.validate(new ObjectMapper().readTree(response));
        assertThat(report.toString(), report.isSuccess(), is(true));
    }
}
