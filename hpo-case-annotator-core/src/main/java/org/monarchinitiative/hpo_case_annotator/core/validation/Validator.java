package org.monarchinitiative.hpo_case_annotator.core.validation;

import java.util.List;

public interface Validator<T> {

    /**
     * Validate given <code>instance</code> and return failures in a list.
     *
     * @param instance to be validated
     * @return {@link List} of {@link ValidationResult}s. The list is empty if the <code>instance</code> is valid
     */
    List<ValidationResult> validate(T instance);
}
