package com.oneplatform.log.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SensitiveFieldSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        if (value == null || value.isBlank()) {
            gen.writeString(value);
            return;
        }
        // Default to FULL mask; per-field strategy handled via a contextual serializer if needed.
        gen.writeString(mask(value));
    }

    private String mask(String value) {
        return "***";
    }

    public static String maskLast4(String value) {
        if (value.length() <= 4) return "****";
        return "*".repeat(value.length() - 4) + value.substring(value.length() - 4);
    }
}