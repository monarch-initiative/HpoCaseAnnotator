package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.databind.JsonNode;

class Util {
    private Util() {
    }

    static Integer readNullableInteger(JsonNode node, String field) {
        if (node.has(field)) {
            JsonNode nodeField = node.get(field);
            return (nodeField.isNull())
                    ? null
                    : nodeField.asInt();
        }
        return null;
    }
}
