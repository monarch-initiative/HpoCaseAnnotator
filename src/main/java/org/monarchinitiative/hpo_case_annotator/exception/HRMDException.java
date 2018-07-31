package org.monarchinitiative.hpo_case_annotator.exception;

public class HRMDException extends Exception {

    public final static long serialVersionUID = 1;

    private String message = null;


    public HRMDException() {
        super();
    }


    public HRMDException(String message) {
        super(message);
        this.message = message;
    }


    public HRMDException(Throwable cause) {
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
