package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.PhenoPacket;

/**
 * This class converts {@link DiseaseCase} to {@link org.phenopackets.schema.v1.PhenoPacket} and back.
 */
public class PhenopacketCodec {

    private PhenopacketCodec() {
        // no-op, static utility class
    }

    public static PhenoPacket diseaseCaseToPhenopacket(DiseaseCase diseaseCase) {
        return null;
    }

    public static DiseaseCase phenopacketToDiseaseCase(PhenoPacket phenoPacket) {
        return null;
    }

}
