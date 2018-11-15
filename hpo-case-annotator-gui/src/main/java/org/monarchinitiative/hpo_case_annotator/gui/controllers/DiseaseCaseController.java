package org.monarchinitiative.hpo_case_annotator.gui.controllers;


import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

public interface DiseaseCaseController {

    /**
     * Set data that will be displayed by this controller.
     *
     * @param diseaseCase {@link DiseaseCase} instance.
     */
    void presentCase(DiseaseCase diseaseCase);


    DiseaseCase getCase();
}
