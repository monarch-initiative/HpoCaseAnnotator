package org.monarchinitiative.hpo_case_annotator.validation;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class ValidationException extends Exception {

    public final static long serialVersionUID = 1;

    private String message = null;


    public ValidationException() {
        super();
    }


    public ValidationException(String message) {
        super(message);
        this.message = message;
    }


    public ValidationException(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return message;
    }
}
