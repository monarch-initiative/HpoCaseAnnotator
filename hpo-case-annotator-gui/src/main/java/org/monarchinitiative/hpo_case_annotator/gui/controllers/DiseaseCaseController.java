package org.monarchinitiative.hpo_case_annotator.gui.controllers;


import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

public interface DiseaseCaseController {

    /**
     * Set data that will be displayed by this controller.
     *
     * @param diseaseCase {@link DiseaseCase} instance
     */
    void presentCase(DiseaseCase diseaseCase);

    /**
     * @return {@link DiseaseCase} representing data in this controller
     */
    DiseaseCase getCase();

    /**
     * @return {@link BooleanBinding} that evaluates to true if the data regarding the entered {@link DiseaseCase} is complete
     */
    BooleanBinding diseaseCaseIsComplete();
}
