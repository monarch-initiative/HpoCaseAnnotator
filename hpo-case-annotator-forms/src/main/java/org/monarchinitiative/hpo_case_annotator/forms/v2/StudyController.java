package org.monarchinitiative.hpo_case_annotator.forms.v2;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public abstract class StudyController<T extends ObservableStudy> extends BindingObservableDataController<T> {

    @FXML
    private TextField studyIdTextField;

    @FXML
    private VBox publication;
    @FXML
    private PublicationController publicationController;

    @FXML
    private VBox variantSummary;
    @FXML
    protected VariantSummaryController variantSummaryController;

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

        Bindings.bindContentBidirectional(variantSummaryController.curatedVariants(), study.variants());

        studyMetadataController.dataProperty().bindBidirectional(study.studyMetadataProperty());
    }

    @Override
    protected void unbind(T study) {
        studyIdTextField.textProperty().unbindBidirectional(study.idProperty());

        publicationController.dataProperty().unbindBidirectional(study.publicationProperty());

        Bindings.unbindContentBidirectional(variantSummaryController.curatedVariants(), study.variants());

        studyMetadataController.dataProperty().unbindBidirectional(study.studyMetadataProperty());
    }

}
