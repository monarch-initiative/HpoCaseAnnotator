package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class ObservableCohortStudy extends ObservableStudy {

    private final ObservableList<ObservableIndividual> members = FXCollections.observableList(new LinkedList<>());

    public ObservableList<ObservableIndividual> members() {
        return members;
    }

}
