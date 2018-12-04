package org.monarchinitiative.hpo_case_annotator.model.io;

import com.google.protobuf.Timestamp;
import org.phenopackets.schema.v1.core.OntologyClass;

import java.time.Instant;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class PhenoPacketTestUtil {

    public static final OntologyClass FEMALE = ontologyClass("PATO:0000383", "female");
    public static final OntologyClass MALE = ontologyClass("PATO:0000384", "male");

    public static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }

    public static Timestamp parseTimestamp(String string) {
        return Timestamp.newBuilder()
                .setSeconds(Instant.parse(string).getEpochSecond())
                .build();
    }

}
