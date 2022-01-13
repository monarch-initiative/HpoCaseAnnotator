package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class ObservablePedigree {
    private final ObservableList<ObservablePedigreeMember> members = FXCollections.observableList(new LinkedList<>());

    public ObservableList<ObservablePedigreeMember> members() {
        return members;
    }
}
