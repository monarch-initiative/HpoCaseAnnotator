package org.monarchinitiative.hpo_case_annotator.validation;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.util.List;

/**
 * Base class for all validators for sharing common resources and utility methods.
 */
public abstract class AbstractValidator {

    /**
     * Message presented when everything is OK.
     */
    final String OKAY = "All right!";

    protected AbstractValidator() {
        // no-op, prevent instantiation through public constructor.
    }


    /**
     * @param s {@link String} that is tested.
     * @return true if given string is either null or equals empty string ("")
     */
    protected static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    public static ValidationResult makeValidationResult(ValidationResult result, String message) {
        result.setMessage(message);
        return result;
    }
    /**
     * @param model {@link DiseaseCaseModel} instance about to be validated
     * @return {@link ValidationResult} with message describing validation outcome
     */
    public abstract ValidationResult validateDiseaseCase(DiseaseCaseModel model);

}
