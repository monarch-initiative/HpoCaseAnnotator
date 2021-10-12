package org.monarchinitiative.hpo_case_annotator.export.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.Phenopacket;

/**
 * This class exposes data (e.g. {@link Phenopacket}s, {@link DiseaseCase}s) suitable for testing the
 * <code>hpo-case-annotator-model</code> classes.
 */
public final class TestResources {

    public static final String SOFTWARE_VERSION = "Hpo Case Annotator";

    // TODO - create an example of FamilyStudy and CohortStudy and test export into Phenopacket.
    //  Drop support of DiseaseCase as we only export data formatted in the most recent (currently v2) model format.


    private TestResources() {
        // private no-op
    }

    public static DiseaseCase benMahmoud2013B3GLCT() {
        return DiseaseCaseModelExample.benMahmoud2013B3GLCT();
    }

    public static DiseaseCase aguilar_Ramirez_2009_C5() {
        return DiseaseCaseModelExample.aguilar_Ramirez_2009_C5();
    }

    public static DiseaseCase structural_beygo_2012_TCOF1_M18662() {
        return DiseaseCaseModelExample.beygo2012TCOF1_M18662_Deletion();
    }

    public static DiseaseCase structuralFictionalBreakend() {
        return DiseaseCaseModelExample.beygo2012TCOF1_M18662_Translocation();
    }
}
