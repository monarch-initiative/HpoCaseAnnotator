package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextArea;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudyMetadata;

import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseStudyIdStep<T extends ObservableStudy> extends BaseStep<T> {

    @FXML
    private TitledTextField studyId;
    @FXML
    private TitledTextArea freeTextMetadata;

    protected BaseStudyIdStep(URL location) {
        super(location);
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(studyId.textProperty(), freeTextMetadata.textProperty());
    }

    @Override
    public void invalidated(Observable obs) {
        // no-op as of now
    }

    @Override
    protected void bind(T data) {
        try {
            valueIsBeingSetProgrammatically = true;

            if (data != null) {
                studyId.textProperty().bindBidirectional(data.idProperty());
                if (data.getStudyMetadata() == null)
                    data.setStudyMetadata(new ObservableStudyMetadata());
                freeTextMetadata.textProperty().bindBidirectional(data.getStudyMetadata().freeTextProperty());
            } else {
                studyId.setText(null);
                freeTextMetadata.setPromptText(null);
            }
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null) {
            studyId.textProperty().unbindBidirectional(data.idProperty());
            if (data.getStudyMetadata() != null)
                freeTextMetadata.textProperty().unbindBidirectional(data.getStudyMetadata().freeTextProperty());
        }
    }
}
