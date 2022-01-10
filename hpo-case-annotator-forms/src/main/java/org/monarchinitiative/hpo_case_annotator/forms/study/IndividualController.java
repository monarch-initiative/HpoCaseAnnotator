package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.beans.value.ChangeListener;

public class IndividualController extends BaseIndividualController<BaseObservableIndividual> {

    // simple "no-op" implementation

    @Override
    protected ChangeListener<BaseObservableIndividual> individualChangeListener() {
        return (obs, old, novel) -> {
            if (old != null)
                unbind(old);

            if (novel != null)
                bind(novel);
        };
    }

}
