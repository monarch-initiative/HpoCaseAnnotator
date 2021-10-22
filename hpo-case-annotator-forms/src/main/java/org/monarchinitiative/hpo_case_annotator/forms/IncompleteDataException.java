package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;

import java.util.List;

public class IncompleteDataException extends HpoCaseAnnotatorException {

    private final List<String> validationMessages;

    public IncompleteDataException() {
        super();
        validationMessages = List.of();
    }

    public IncompleteDataException(List<String> validationMessages, String message) {
        super(message);
        this.validationMessages = validationMessages;
    }

    public IncompleteDataException(List<String> validationMessages, String message, Throwable cause) {
        super(message, cause);
        this.validationMessages = validationMessages;
    }

    public IncompleteDataException(List<String> validationMessages, Throwable cause) {
        super(cause);
        this.validationMessages = validationMessages;
    }

    protected IncompleteDataException(List<String> validationMessages, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.validationMessages = validationMessages;
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
