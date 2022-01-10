package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.VariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;

public class FamilyStudyController implements ComponentController<FamilyStudy> {

    private FamilyStudy familyStudy;

    @FXML
    private VBox variantSummary;
    @FXML
    private VariantSummaryController variantSummaryController;
    @FXML
    private VBox pedigree;
    @FXML
    private PedigreeController pedigreeController;

    @FXML
    private void initialize() {
        // pedigreeController.variants() must not be modified directly from now on!
        Bindings.bindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());
    }

    @Override
    public void presentComponent(FamilyStudy study) {
        // First the individuals
        pedigreeController.presentComponent(study.pedigree());

        // Then the variants
        variantSummaryController.curatedVariants()
                .addAll(study.variants());

        // cache the study
        // TODO - write controllers for metadata and publication
        familyStudy = study;
    }

    @Override
    public FamilyStudy getComponent() throws InvalidComponentDataException {
        return FamilyStudy.of(familyStudy.publication(),
                variantSummaryController.curatedVariants(),
                pedigreeController.getComponent(),
                familyStudy.studyMetadata());
    }

}
