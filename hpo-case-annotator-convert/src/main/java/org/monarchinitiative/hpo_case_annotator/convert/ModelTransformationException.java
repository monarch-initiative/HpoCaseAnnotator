package org.monarchinitiative.hpo_case_annotator.convert;

import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;

public class ModelTransformationException extends HpoCaseAnnotatorException {

    public ModelTransformationException() {
        super();
    }

    public ModelTransformationException(String message) {
        super(message);
    }

    public ModelTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelTransformationException(Throwable cause) {
        super(cause);
    }

    protected ModelTransformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
