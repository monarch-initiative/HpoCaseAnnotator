package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class ObservableCohortStudy extends ObservableStudy {

    private final ObservableList<ObservableIndividual> members = FXCollections.observableList(new LinkedList<>());

    public ObservableList<ObservableIndividual> members() {
        return members;
    }

}
