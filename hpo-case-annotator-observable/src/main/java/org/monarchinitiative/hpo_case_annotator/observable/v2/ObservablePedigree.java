package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

public class ObservablePedigree implements Observable, Pedigree {

    private final ListProperty<ObservablePedigreeMember> members = new SimpleListProperty<>(this, "members", FXCollections.observableArrayList(ObservablePedigreeMember.EXTRACTOR));

    public ObservablePedigree() {
    }

    public ObservablePedigree(Pedigree pedigree) {
        if (pedigree != null) {
            for (PedigreeMember member : pedigree.getMembers()) {
                if (member != null)
                    members.add(new ObservablePedigreeMember(member));
            }
        }
    }

    public ListProperty<ObservablePedigreeMember> membersProperty() {
        return members;
    }

    @Override
    public ObservableList<? extends PedigreeMember> getMembers() {
        return members;
    }

    public void setMembers(ObservableList<ObservablePedigreeMember> members) {
        this.members.set(members);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        members.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        members.removeListener(listener);
    }

    @Override
    public String toString() {
        return "ObservablePedigree{" +
                "members=" + members.get() +
                '}';
    }
}
