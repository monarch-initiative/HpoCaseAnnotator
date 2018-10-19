package org.monarchinitiative.hpo_case_annotator.model;

import org.monarchinitiative.hpo_case_annotator.HpoCaseAnnotatorException;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class TransformationException extends HpoCaseAnnotatorException {

    public TransformationException() {
    }


    public TransformationException(String message) {
        super(message);
    }


    public TransformationException(Throwable cause) {
        super(cause);
    }
}
