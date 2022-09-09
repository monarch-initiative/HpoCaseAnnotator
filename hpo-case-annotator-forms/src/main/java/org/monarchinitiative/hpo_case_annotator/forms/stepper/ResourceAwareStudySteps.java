package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

/**
 * {@link ResourceAwareStudySteps} are {@link StudyResourcesAware}.
 *
 * @param <T> type of {@link ObservableStudy} to work with.
 */
public abstract class ResourceAwareStudySteps<T> extends BaseSteps<T> implements StudyResourcesAware {

    protected final StudyResources studyResources;

    protected ResourceAwareStudySteps() {
        studyResources = new StudyResources();
    }

    @Override
    public StudyResources getStudyResources() {
        return studyResources;
    }

}
