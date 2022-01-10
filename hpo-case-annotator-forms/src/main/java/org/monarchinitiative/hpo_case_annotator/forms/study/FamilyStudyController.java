package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;

public class FamilyStudyController implements ComponentController<FamilyStudy> {

    @FXML
    private VBox publication;
    @FXML
    private PublicationController publicationController;
    @FXML
    private VBox variantSummary;
    @FXML
    private VariantSummaryController variantSummaryController;
    @FXML
    private VBox pedigree;
    @FXML
    private PedigreeController pedigreeController;
    @FXML
    private VBox metadata;
    @FXML
    private MetadataController metadataController;

    @FXML
    private void initialize() {
        // pedigreeController.variants() must not be modified directly from now on!
        Bindings.bindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());
    }

    @Override
    public void presentComponent(FamilyStudy study) {
        publicationController.presentComponent(study.publication());
        pedigreeController.presentComponent(study.pedigree());
        variantSummaryController.curatedVariants().addAll(study.variants());
        metadataController.presentComponent(study.studyMetadata());
    }

    @Override
    public FamilyStudy getComponent() throws InvalidComponentDataException {
        return FamilyStudy.of(publicationController.getComponent(),
                variantSummaryController.curatedVariants(),
                pedigreeController.getComponent(),
                metadataController.getComponent());
    }

}
