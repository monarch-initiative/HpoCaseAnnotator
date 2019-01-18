package org.monarchinitiative.hpo_case_annotator.gui.controllers;


import javafx.beans.binding.BooleanBinding;

public interface DataController<T> {

    /**
     * Set data that will be displayed by this controller.
     *
     * @param data {@link T} instance
     */
    void presentData(T data);

    /**
     * @return {@link T} representing data in this controller
     */
    T getData();

    /**
     * @return {@link BooleanBinding} that evaluates to true if the data regarding the entered {@link T} is complete
     */
    boolean isComplete();
}
