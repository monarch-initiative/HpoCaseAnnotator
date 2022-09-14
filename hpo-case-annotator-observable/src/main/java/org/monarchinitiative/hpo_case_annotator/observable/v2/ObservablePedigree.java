package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.util.stream.Stream;

public class ObservablePedigree extends DeepObservable implements Pedigree {

    public static final Callback<ObservablePedigree, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.members};
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
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(members);
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservablePedigree{" +
                "members=" + members.get() +
                '}';
    }
}
