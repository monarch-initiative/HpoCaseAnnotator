package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;

public class ObservablePedigree implements Pedigree, Updateable<Pedigree> {
    private final ObservableList<ObservablePedigreeMember> members = FXCollections.observableList(new LinkedList<>());

    public ObservableList<ObservablePedigreeMember> membersList() {
        return members;
    }

    @Override
    public List<? extends PedigreeMember> getMembers() {
        return members;
    }

    @Override
    public <U extends Pedigree> void update(U data) {
        if (data == null)
            members.clear();
        else
            Updateable.updateObservableList(data.getMembers(), members, ObservablePedigreeMember::new);
    }
}
