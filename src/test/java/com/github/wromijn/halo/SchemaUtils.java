package com.github.wromijn.halo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaUtils {

    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public void assertSerializationMatchesSchema(Object o, String schemaPath) {
        String response = objectMapper.writeValueAsString(o);
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

}
