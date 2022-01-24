package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PedigreeDeserializer extends StdDeserializer<Pedigree> {

    public PedigreeDeserializer() {
        this(Pedigree.class);
    }

    protected PedigreeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Pedigree deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        List<PedigreeMember> pedigreeMemberList = new LinkedList<>();
        Iterator<JsonNode> elementsIterator = node.get("members").elements();
        while (elementsIterator.hasNext()) {
            pedigreeMemberList.add(codec.treeToValue(elementsIterator.next(), PedigreeMember.class));
        }

        return Pedigree.of(pedigreeMemberList);
    }
}
