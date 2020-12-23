package org.monarchinitiative.hpo_case_annotator.core.validation;

import java.util.Objects;

public class ValidationResult {

    private static final ValidationResult PASS = new ValidationResult(Status.PASS, "");

    private final Status status;

    private final String message;

    private ValidationResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ValidationResult pass() {
        return PASS;
    }

    public static ValidationResult fail(String message) {
        Objects.requireNonNull(message);
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Failed validation result message cannot be empty");
        }
        return new ValidationResult(Status.FAIL, message);
    }

    public static ValidationResult warn(String message) {
        Objects.requireNonNull(message);
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Warning validation result message cannot be empty");
        }
        return new ValidationResult(Status.WARN, message);
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return status == Status.PASS;
    }

    public boolean isWarning() {
        return status == Status.WARN;
    }

    public boolean notValid() {
        return status == Status.FAIL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return status == that.status &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    private enum Status {PASS, WARN, FAIL}
}
