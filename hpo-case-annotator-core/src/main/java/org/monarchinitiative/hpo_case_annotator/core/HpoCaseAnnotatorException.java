package org.monarchinitiative.hpo_case_annotator.core;

public class HpoCaseAnnotatorException extends Exception {

    public final static long serialVersionUID = 1;

    private String message = null;


    public HpoCaseAnnotatorException() {
        super();
    }


    public HpoCaseAnnotatorException(String message) {
        super(message);
        this.message = message;
    }


    public HpoCaseAnnotatorException(Throwable cause) {
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
