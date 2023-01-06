package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.databind.JsonNode;

public class Util {
    private Util() {
    }

    public static Integer readNullableInteger(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return nodeIsNull(field)
                ? null
                : field.asInt();
    }

    public static String readNullableString(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return nodeIsNull(field)
                ? null
                : field.asText();
    }

    public static boolean nodeIsNull(JsonNode node) {
        return node == null || node.isNull();
    }
}
