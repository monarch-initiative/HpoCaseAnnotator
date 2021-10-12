package org.monarchinitiative.hpo_case_annotator.model;

public class HpoCaseAnnotatorException extends Exception {

    public HpoCaseAnnotatorException() {
        super();
    }

    public HpoCaseAnnotatorException(String message) {
        super(message);
    }

    public HpoCaseAnnotatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HpoCaseAnnotatorException(Throwable cause) {
        super(cause);
    }

    protected HpoCaseAnnotatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
