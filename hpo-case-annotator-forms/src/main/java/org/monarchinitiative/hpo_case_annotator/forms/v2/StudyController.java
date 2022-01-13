package org.monarchinitiative.hpo_case_annotator.forms.v2;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StudyController<T extends ObservableStudy> extends BindingDataController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyController.class);

    @FXML
    private TextField studyIdTextField;

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(T study) {
        studyIdTextField.textProperty().bindBidirectional(study.idProperty());
    }

    @Override
    protected void unbind(T study) {
        studyIdTextField.textProperty().unbindBidirectional(study.idProperty());
    }

}
