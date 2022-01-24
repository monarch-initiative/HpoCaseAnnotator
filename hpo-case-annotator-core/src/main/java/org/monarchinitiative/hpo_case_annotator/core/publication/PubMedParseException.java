package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class PubMedParseException extends HpoCaseAnnotatorException {

    public PubMedParseException() {
    }


    public PubMedParseException(String message) {
        super(message);
    }


    public PubMedParseException(Throwable cause) {
        super(cause);
    }
}
