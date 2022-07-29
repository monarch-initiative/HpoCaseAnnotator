package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.List;

public class ObservablePedigree implements Pedigree, Updateable<Pedigree> {
    private final ListProperty<ObservablePedigreeMember> members = new SimpleListProperty<>(this, "members", FXCollections.observableArrayList());

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
    public <U extends Pedigree> void update(U data) {
        if (data == null)
            members.clear();
        else
            Updateable.updateObservableList(data.getMembers(), members, ObservablePedigreeMember::new);
    }
}
