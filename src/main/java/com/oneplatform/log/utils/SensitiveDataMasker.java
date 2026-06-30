package com.oneplatform.log.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;
import java.util.regex.Pattern;

public class SensitiveDataMasker {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // field names checked case-insensitively, substring match
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "pwd", "otp", "cvv", "cvv2", "card_number", "card_no",
            "pin", "token", "access_token", "refresh_token", "secret", "apikey",
            "ssn", "ssn4", "authorization"
    );

    // catches raw card numbers / long digit sequences even inside free-text strings
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\b(?:\\d[ -]?){13,19}\\b");

    public static String maskJson(Object payload) {
        try {
            JsonNode node = MAPPER.valueToTree(payload);
            maskNode(node);
            return MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            return "[unserializable: " + payload.getClass().getSimpleName() + "]";
        }
    }

    private static void maskNode(JsonNode node) {
        if (node == null) return;

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.fieldNames().forEachRemaining(fieldName -> {
                JsonNode child = obj.get(fieldName);

                if (isSensitiveKey(fieldName)) {
                    obj.put(fieldName, maskSensitiveValue(fieldName, child));
                } else if (child.isObject() || child.isArray()) {
                    maskNode(child);
                } else if (child.isTextual()) {
                    obj.put(fieldName, maskFreeText(child.asText()));
                }
            });
        } else if (node.isArray()) {
            node.forEach(SensitiveDataMasker::maskNode);
        }
    }

    private static String maskSensitiveValue(String fieldName, JsonNode child) {
        if (child == null || !child.isTextual() || child.asText().isBlank()) {
            return "***";
        }

        String lower = fieldName.toLowerCase();
        if (lower.contains("token")) {
            return TokenMaskUtil.truncateToken(child.asText());
        }
        return "***";
    }

    private static boolean isSensitiveKey(String key) {
        String lower = key.toLowerCase();
        return SENSITIVE_KEYS.stream().anyMatch(lower::contains);
    }

    private static String maskFreeText(String text) {
        return CARD_NUMBER_PATTERN.matcher(text).replaceAll("[CARD_REDACTED]");
    }
}