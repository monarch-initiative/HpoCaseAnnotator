package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.value.ChangeListener;
import org.monarchinitiative.hpo_case_annotator.forms.observable.BaseObservableIndividual;

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
