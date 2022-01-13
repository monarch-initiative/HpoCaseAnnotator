package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableStudyMetadata;

public class StudyMetadataController extends BindingDataController<ObservableStudyMetadata> {

    private final ObjectProperty<ObservableStudyMetadata> studyMetadata = new SimpleObjectProperty<>(this, "studyMetadata", new ObservableStudyMetadata());

    @FXML
    private TextArea metadataTextArea;

    @FXML
    protected void initialize() {
        super.initialize();

    }

    @Override
    protected void bind(ObservableStudyMetadata metadata) {
        metadataTextArea.textProperty().bindBidirectional(metadata.freeTextProperty());
    }

    @Override
    protected void unbind(ObservableStudyMetadata metadata) {
        metadataTextArea.textProperty().unbindBidirectional(metadata.freeTextProperty());
    }

    @Override
    public ObjectProperty<ObservableStudyMetadata> dataProperty() {
        return studyMetadata;
    }
}
