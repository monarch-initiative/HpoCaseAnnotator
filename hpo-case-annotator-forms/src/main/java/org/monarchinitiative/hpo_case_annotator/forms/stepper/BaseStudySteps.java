package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public abstract class BaseStudySteps<T extends ObservableStudy> implements StudyResourcesAware {

    protected final StudyResources studyResources = new StudyResources();

    protected final ListProperty<Step<T>> steps = new SimpleListProperty<>(FXCollections.observableArrayList());
    protected BaseStudySteps() {
        configureSteps();
    }

    protected abstract void configureSteps();


    public ListProperty<Step<T>> stepsProperty() {
        return steps;
    }

    @Override
    public StudyResources getStudyResources() {
        return studyResources;
    }
}
