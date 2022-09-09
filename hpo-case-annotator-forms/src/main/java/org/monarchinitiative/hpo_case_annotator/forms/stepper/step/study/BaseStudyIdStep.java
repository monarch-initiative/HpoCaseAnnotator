package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseStudyIdStep<T extends ObservableStudy> extends BaseStep<T> {

    // TODO - add free text

    @FXML
    private TitledTextField studyId;

    protected BaseStudyIdStep(URL location) {
        super(location);
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(studyId.textProperty());
    }

    @Override
    public void invalidated(Observable obs) {
        // no-op as of now
    }

    @Override
    protected void bind(T data) {
        try {
            valueIsBeingSetProgrammatically = true;

            if (data != null)
                studyId.textProperty().bindBidirectional(data.idProperty());
            else
                studyId.setText(null);
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            studyId.textProperty().unbindBidirectional(data.idProperty());
    }
}
