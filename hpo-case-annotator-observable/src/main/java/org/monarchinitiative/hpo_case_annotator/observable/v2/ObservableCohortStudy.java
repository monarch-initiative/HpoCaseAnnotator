package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ObservableCohortStudy extends ObservableStudy<CohortStudy> implements CohortStudy, Updateable<CohortStudy> {

    private final ObservableList<ObservableIndividual> members = FXCollections.observableList(new LinkedList<>());

    public ObservableList<ObservableIndividual> observableMembers() {
        return members;
    }

    @Override
    public List<? extends Individual> getMembers() {
        return members;
    }

    @Override
    public <U extends CohortStudy> void update(U data) {
        super.update(data);
        if (data == null)
            members.clear();
        else
            Updateable.updateObservableList(data.getMembers(), members, ObservableIndividual::new);
    }
}
