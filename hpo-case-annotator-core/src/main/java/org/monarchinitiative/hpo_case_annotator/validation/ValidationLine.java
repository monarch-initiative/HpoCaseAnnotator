package org.monarchinitiative.hpo_case_annotator.validation;

/**
 * This POJO contains data that are being presented as lines in {@link javafx.scene.control.TableView} displayed to user
 * after clicking Validate/Validate all XML files of Validate/Validate current model Menu entries.
 */
public class ValidationLine {

    /* Disease case model name - First author & year */
    private String modelName;

    /* Name of the class that has been used to validate model */
    private String validatorName;

    /* Value of ValidationResult enum */
    private String validationResult;

    /* Error message, if any */
    private String errorMessage;


    public ValidationLine(String modelName, String validatorName, String validationResult, String errorMessage) {
        this.modelName = modelName;
        this.validatorName = validatorName;
        this.validationResult = validationResult;
        this.errorMessage = errorMessage;
    }


    public ValidationLine() {
        // no-op, for being a nice behaved POJO..
    }


    public String getModelName() {
        return modelName;
    }


    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public String getValidatorName() {
        return validatorName;
    }


    public void setValidatorName(String validatorName) {
        this.validatorName = validatorName;
    }


    public String getValidationResult() {
        return validationResult;
    }


    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ValidationLine{");
        sb.append("modelName='").append(modelName).append('\'');
        sb.append(", validatorName='").append(validatorName).append('\'');
        sb.append(", validationResult='").append(validationResult).append('\'');
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
