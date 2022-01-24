package org.monarchinitiative.hpo_case_annotator.forms;

public interface ComponentController<T> {

    /**
     * @param component {@link T} component that will be displayed by this controller
     */
    void presentComponent(T component);

    /**
     * @return {@link T} representing component in this controller
     */
    T getComponent() throws InvalidComponentDataException;


    // TODO - find out a way how to validate the content
}
