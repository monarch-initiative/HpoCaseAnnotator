package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

class Util {
    private Util() {
    }

    static void writeNullableNumber(JsonGenerator gen, String field, Integer value) throws IOException {
        if (value == null)
            gen.writeNullField(field);
        else
            gen.writeNumberField(field, value);
    }
}
