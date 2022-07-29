package org.monarchinitiative.hpo_case_annotator.forms;

public interface DataController<T> {

    /**
     * @return {@link T} representing the actual state of the controller, possibly {@code null} value.
     */
    T getData();

    /**
     * Set data state of the controller.
     *
     * @param data instance of {@link T} or {@code null}.
     */
    void setData(T data);

}
