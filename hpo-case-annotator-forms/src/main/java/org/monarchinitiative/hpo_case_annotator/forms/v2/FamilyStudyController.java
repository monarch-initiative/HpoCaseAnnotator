package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FamilyStudyController extends StudyController<ObservableFamilyStudy> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyStudyController.class);
    private final ObjectProperty<ObservableFamilyStudy> study = new SimpleObjectProperty<>(this, "study", new ObservableFamilyStudy());

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
    private VBox studyMetadata;
    @FXML
    private StudyMetadataController studyMetadataController;

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    public ObjectProperty<ObservableFamilyStudy> dataProperty() {
        return study;
    }

    @Override
    protected void bind(ObservableFamilyStudy study) {
        super.bind(study);
        publicationController.dataProperty().bindBidirectional(study.publicationProperty());

        // pedigreeController.variants() must not be modified directly from now on!
        Bindings.bindContentBidirectional(variantSummaryController.curatedVariants(), study.variants());
        Bindings.bindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());

        pedigreeController.dataProperty().bindBidirectional(study.pedigreeProperty());

        studyMetadataController.dataProperty().bindBidirectional(study.studyMetadataProperty());
    }

    @Override
    protected void unbind(ObservableFamilyStudy study) {
        super.unbind(study);
        publicationController.dataProperty().unbindBidirectional(study.publicationProperty());

        // unbinding should be in reverse order?
        Bindings.unbindContentBidirectional(variantSummaryController.curatedVariants(), study.variants());
        Bindings.unbindContent(pedigreeController.curatedVariants(), variantSummaryController.curatedVariants());

        pedigreeController.dataProperty().unbindBidirectional(study.pedigreeProperty());
        studyMetadataController.dataProperty().unbindBidirectional(study.studyMetadataProperty());
    }

}
