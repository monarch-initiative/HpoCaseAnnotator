package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudyController extends StudyController<ObservableCohortStudy> {

    private final ObjectProperty<ObservableCohortStudy> study = new SimpleObjectProperty<>(this, "study", new ObservableCohortStudy());

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    public ObjectProperty<ObservableCohortStudy> dataProperty() {
        return study;
    }


}
