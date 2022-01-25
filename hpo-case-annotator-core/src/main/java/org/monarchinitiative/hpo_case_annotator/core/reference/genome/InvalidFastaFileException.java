package org.monarchinitiative.hpo_case_annotator.core.reference.genome;


import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;

/**
 * @author Daniel Danis
 */
public class InvalidFastaFileException extends HpoCaseAnnotatorException {

    public InvalidFastaFileException() {
        super();
    }

    public InvalidFastaFileException(String message) {
        super(message);
    }

    public InvalidFastaFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFastaFileException(Throwable cause) {
        super(cause);
    }

    protected InvalidFastaFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
