package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

import java.util.stream.Stream;

public class ObservableCohortStudy extends ObservableStudy implements CohortStudy {

    public static final Callback<ObservableCohortStudy, Observable[]> EXTRACTOR = obs -> {
        Observable[] parent = ObservableStudy.EXTRACTOR.call(obs);
        Observable[] current = new Observable[]{obs.members};

        Observable[] result = new Observable[parent.length + current.length];
        System.arraycopy(parent, 0, result, 0, parent.length);
        System.arraycopy(current, 0, result, parent.length, current.length);

        return result;
    };

    private final ListProperty<ObservableIndividual> members = new SimpleListProperty<>(this, "members", FXCollections.observableArrayList(ObservableIndividual.EXTRACTOR));

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
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.concat(
                super.objectProperties(),
                Stream.of(members)
        );
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
