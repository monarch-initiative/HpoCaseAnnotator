package org.monarchinitiative.hpo_case_annotator.forms;

public interface DataEditController<T> {

    void setInitialData(T data);

    T getEditedData();

}
