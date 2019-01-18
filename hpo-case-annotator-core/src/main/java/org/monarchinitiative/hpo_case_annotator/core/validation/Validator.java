package org.monarchinitiative.hpo_case_annotator.core.validation;

import java.util.List;

public interface Validator<T> {

    List<ValidationResult> validate(T instance);
}
