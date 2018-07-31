package org.monarchinitiative.hpo_case_annotator.model;

/**
 * Write down specification for future data model class here.
 */
public interface FutureModel {

    /**
     * Return true if mandatory information have been provided and the model is worth saving.
     *
     * @return true or false.
     */
    boolean isComplete();

    /**
     * Return version of this model.
     *
     * @return {@link HRMDModelVersion} object representing the model version.
     */
    HRMDModelVersion getVersion();
}
