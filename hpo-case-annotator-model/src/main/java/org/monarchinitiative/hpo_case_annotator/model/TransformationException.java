package org.monarchinitiative.hpo_case_annotator.model;


/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class TransformationException extends Exception {

    public TransformationException() {
    }


    public TransformationException(String message) {
        super(message);
    }


    public TransformationException(Throwable cause) {
        super(cause);
    }
}
