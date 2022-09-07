package org.monarchinitiative.hpo_case_annotator.forms.v2;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public abstract class StudyController<T extends ObservableStudy> extends BindingObservableDataController<T> {

    @FXML
    private TextField studyIdTextField;

    @FXML
    private VBox publication;
    @FXML
    private PublicationController publicationController;

    @FXML
    protected VariantSummary variantSummary;

    @FXML
    private VBox studyMetadata;
    @FXML
    private StudyMetadataController studyMetadataController;


    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(T study) {
        studyIdTextField.textProperty().bindBidirectional(study.idProperty());

        publicationController.dataProperty().bindBidirectional(study.publicationProperty());

        variantSummary.variants().bindBidirectional(study.variantsProperty());

        studyMetadataController.dataProperty().bindBidirectional(study.studyMetadataProperty());
    }

    @Override
    protected void unbind(T study) {
        studyIdTextField.textProperty().unbindBidirectional(study.idProperty());

        publicationController.dataProperty().unbindBidirectional(study.publicationProperty());

        variantSummary.variants().unbindBidirectional(study.variantsProperty());

        studyMetadataController.dataProperty().unbindBidirectional(study.studyMetadataProperty());
    }

}
