package org.monarchinitiative.hpo_case_annotator.validation;

public enum ValidationResult {

    PASSED("PASSED"),
    FAILED("FAILED"),
    UNAPPLICABLE("UNAPPLICABLE");

    ValidationResult(String message) {
        this.message = message;
    }

    private String message;


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

}
