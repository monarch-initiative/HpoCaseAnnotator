package org.monarchinitiative.hpo_case_annotator.app.controller;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.study.BaseStudyComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.nio.file.Path;

class StudyWrapper<T extends ObservableStudy> {
    private final BaseStudyComponent<T> component;
    private final ObjectProperty<Path> studyPath = new SimpleObjectProperty<>();
    private final boolean addEditHistoryItem;

    StudyWrapper(BaseStudyComponent<T> component,
                 Path studyPath,
                 boolean addEditHistoryItem) {
        this.component = component;
        this.studyPath.set(studyPath);
        this.addEditHistoryItem = addEditHistoryItem;
    }

    public BaseStudyComponent<T> component() {
        return component;
    }

    public void setStudyPath(Path studyPath) {
        this.studyPath.set(studyPath);
    }

    public Path getStudyPath() {
        return studyPath.get();
    }

    public ObjectProperty<Path> studyPathProperty() {
        return studyPath;
    }

    public boolean addEditHistoryItem() {
        return addEditHistoryItem;
    }

    @Override
    public String toString() {
        return "StudyWrapper{" +
                "study=" + component +
                ", studyPath=" + studyPath.get() +
                ", addEditHistoryItem=" + addEditHistoryItem +
                '}';
    }
}
