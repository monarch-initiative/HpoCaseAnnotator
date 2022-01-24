package org.monarchinitiative.hpo_case_annotator.app.controller;


import java.nio.file.Path;
import java.util.Objects;

public class StudyWrapper<T> {

    private final T study;
    private Path studyPath;

    public static <T> StudyWrapper<T> of(T study) {
        return of(study, null);
    }

    public static <T> StudyWrapper<T> of(T study, Path studyPath) {
        return new StudyWrapper<>(study, studyPath);
    }

    private StudyWrapper(T controller, Path studyPath) {
        this.study = controller;
        this.studyPath = studyPath;
    }

    public T study() {
        return study;
    }

    public Path studyPath() {
        return studyPath;
    }

    public void setStudyPath(Path studyPath) {
        this.studyPath = studyPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyWrapper<?> that = (StudyWrapper<?>) o;
        return Objects.equals(study, that.study) && Objects.equals(studyPath, that.studyPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(study, studyPath);
    }

    @Override
    public String toString() {
        return "StudyWrapper{" +
                "study=" + study +
                ", studyPath=" + studyPath +
                '}';
    }
}
