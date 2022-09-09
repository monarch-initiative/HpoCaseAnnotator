package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Note: {@link #configureSteps()} must be called after creating the steps.
 * @param <T>
 */
public abstract class BaseSteps<T> {

    protected final ListProperty<Step<T>> steps = new SimpleListProperty<>(FXCollections.observableArrayList());

    protected BaseSteps() {
    }

    // TODO - having to call `configureSteps()` after each steps creation is error-prone. Can we do better?
    public abstract BaseSteps<T> configureSteps();

    public ListProperty<Step<T>> stepsProperty() {
        return steps;
    }

    public ObservableList<Step<T>> getSteps() {
        return steps.get();
    }
}
