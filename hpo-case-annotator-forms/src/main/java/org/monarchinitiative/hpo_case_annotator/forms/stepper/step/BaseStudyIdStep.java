package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.net.URL;

public class BaseStudyIdStep<T extends ObservableStudy> extends BaseStep<T> {

    @FXML
    private TitledTextField studyId;

    protected BaseStudyIdStep(URL location) {
        super(location);
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        studyId.textProperty().bindBidirectional(data.idProperty());
    }

    @Override
    protected void unbind(T data) {
        studyId.textProperty().unbindBidirectional(data.idProperty());
    }
}
