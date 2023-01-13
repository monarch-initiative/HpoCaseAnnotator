package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class OntologyClassDeserializer extends StdDeserializer<OntologyClass> {

    public OntologyClassDeserializer() {
        this(OntologyClass.class);
    }

    protected OntologyClassDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OntologyClass deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        TermId id;
        try {
            id = TermId.of(node.get("id").asText());
        } catch (PhenolRuntimeException e) {
            throw new IOException(e);
        }

        String label = node.get("label").asText();

        return OntologyClass.of(id, label);
    }
}
