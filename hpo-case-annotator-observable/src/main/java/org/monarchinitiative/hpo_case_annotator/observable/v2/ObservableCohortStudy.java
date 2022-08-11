package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

public class ObservableCohortStudy extends ObservableStudy implements CohortStudy {

    public static ObservableCohortStudy defaultInstance() {
        ObservableCohortStudy instance = new ObservableCohortStudy();
        instance.setPublication(ObservablePublication.defaultInstance());
        instance.setStudyMetadata(ObservableStudyMetadata.defaultInstance());
        return instance;
    }

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
    public String toString() {
        return "ObservableCohortStudy{" +
                "id=" + getId() +
                ", publication=" + getPublication() +
                ", variants=" + getVariants() +
                ", members=" + members.get() +
                ", studyMetadata=" + getStudyMetadata() +
                "} ";
    }
}
