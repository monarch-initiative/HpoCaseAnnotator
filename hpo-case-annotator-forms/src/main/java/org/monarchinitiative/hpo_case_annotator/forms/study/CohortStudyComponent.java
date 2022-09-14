package org.monarchinitiative.hpo_case_annotator.forms.study;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudyComponent extends BaseStudyComponent<ObservableCohortStudy> {

    public CohortStudyComponent() {
        super(CohortStudyComponent.class.getResource("CohortStudy.fxml"));
    }

}
