package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ObservableCohortStudy extends ObservableStudy implements CohortStudy, Updateable<CohortStudy> {

    private final ListProperty<ObservableIndividual> members = new SimpleListProperty<>(this, "members", FXCollections.observableArrayList());

    public ObservableCohortStudy() {
    }

    public ObservableCohortStudy(CohortStudy cohortStudy) {
        super(cohortStudy);
        if (cohortStudy != null) {
            for (Individual member : cohortStudy.getMembers())
                members.add(new ObservableIndividual(member));
        }
    }

    public void setMembers(ObservableList<ObservableIndividual> members) {
        this.members.set(members);
    }

    public ListProperty<ObservableIndividual> membersProperty() {
        return members;
    }

    @Override
    public ObservableList<? extends Individual> getMembers() {
        return members;
    }

    @Override
    public <U extends CohortStudy> void update(U data) {
        updateStudy(data);
        if (data == null)
            members.clear();
        else
            Updateable.updateObservableList(data.getMembers(), members, ObservableIndividual::new);
    }
}
