package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;

public class InvalidComponentDataException extends HpoCaseAnnotatorException {

    public InvalidComponentDataException() {
        super();
    }

    public InvalidComponentDataException(String message) {
        super(message);
    }

    public InvalidComponentDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidComponentDataException(Throwable cause) {
        super(cause);
    }

    protected InvalidComponentDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
