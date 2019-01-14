package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

/**
 * Base class for all validators for sharing common resources and utility methods.
 */
@Deprecated // use Validator<T> implementations instead
public abstract class AbstractValidator {

    /**
     * Message presented when everything is OK.
     */
    final String OKAY = "All right!";


    /**
     * @param s {@link String} that is tested.
     * @return true if given string is either null or equals empty string ("")
     */
    protected static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }


    public static ValidationResult makeValidationResult(ValidationResult result, String message) {
//        result.setMessage(message);
        return result;
    }


    protected AbstractValidator() {
        // no-op, prevent instantiation through public constructor.
    }


    /**
     * @param model {@link DiseaseCase} instance about to be validated
     * @return {@link ValidationResult} with message describing validation outcome
     */
    public abstract ValidationResult validateDiseaseCase(DiseaseCase model);

}
