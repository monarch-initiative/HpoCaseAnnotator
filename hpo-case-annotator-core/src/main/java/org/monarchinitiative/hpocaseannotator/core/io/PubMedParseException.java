package org.monarchinitiative.hpocaseannotator.core.io;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class PubMedParseException extends Exception {

    public PubMedParseException() {
        super();
    }


    public PubMedParseException(String msg) {
        super(msg);
    }


    public PubMedParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
