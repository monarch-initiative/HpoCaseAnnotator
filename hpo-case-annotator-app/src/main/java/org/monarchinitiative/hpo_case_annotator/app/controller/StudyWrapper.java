package org.monarchinitiative.hpo_case_annotator.app.controller;


import org.monarchinitiative.hpo_case_annotator.forms.study.BaseStudyComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.nio.file.Path;

class StudyWrapper<T extends ObservableStudy> {
    private final BaseStudyComponent<T> component;
    private Path studyPath;

    public static <T extends ObservableStudy> StudyWrapper<T> of(BaseStudyComponent<T> component) {
        return of(component, null);
    }

    public static <T extends ObservableStudy> StudyWrapper<T> of(BaseStudyComponent<T> component, Path studyPath) {
        return new StudyWrapper<>(component, studyPath);
    }

    private StudyWrapper(BaseStudyComponent<T> component, Path studyPath) {
        this.component = component;
        this.studyPath = studyPath;
    }

    public BaseStudyComponent<T> component() {
        return component;
    }

    public Path studyPath() {
        return studyPath;
    }

    public void setStudyPath(Path studyPath) {
        this.studyPath = studyPath;
    }

    @Override
    public String toString() {
        return "StudyWrapper{" +
                "study=" + component +
                ", studyPath=" + studyPath +
                '}';
    }
}
