package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudyController extends StudyController<ObservableCohortStudy> {

    private final ObjectProperty<ObservableCohortStudy> study = new SimpleObjectProperty<>(this, "study", new ObservableCohortStudy());

    @FXML
    private VBox cohort;
    @FXML
    private CohortController cohortController;

    @FXML
    protected void initialize() {
        super.initialize();
    }


    @Override
    protected void bind(ObservableCohortStudy study) {
        super.bind(study);

        Bindings.bindContent(cohortController.curatedVariants(), variantSummaryController.curatedVariants());
        Bindings.bindContentBidirectional(cohortController.members(), study.membersProperty());
    }

    @Override
    protected void unbind(ObservableCohortStudy study) {
        super.unbind(study);

        Bindings.unbindContent(cohortController.curatedVariants(), variantSummaryController.curatedVariants());
        Bindings.unbindContentBidirectional(cohortController.members(), study.membersProperty());
    }

    @Override
    public ObjectProperty<ObservableCohortStudy> dataProperty() {
        return study;
    }
}
