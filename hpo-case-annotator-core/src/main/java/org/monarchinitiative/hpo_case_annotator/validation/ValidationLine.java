package org.monarchinitiative.hpo_case_annotator.validation;

/**
 * This POJO contains data that are being presented as lines in {@link javafx.scene.control.TableView} displayed to user
 * after clicking Validate/Validate all XML files of Validate/Validate current model Menu entries.
 */
public class ValidationLine {

    /* Disease case model name - First author & year */
    private final String modelName;

    /* Name of the class that has been used to validate model */
    private final String validatorName;

    /* Value of ValidationResult enum */
    private final ValidationResult validationResult;

    public ValidationLine(String modelName, String validatorName, ValidationResult result) {
        this.modelName = modelName;
        this.validatorName = validatorName;
        this.validationResult = result;
    }


    public String getModelName() {
        return modelName;
    }



    public String getValidatorName() {
        return validatorName;
    }



    public ValidationResult getValidationResult() {
        return validationResult;
    }



    public String getErrorMessage() {
        return validationResult.getMessage();
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ValidationLine{");
        sb.append("modelName='").append(modelName).append('\'');
        sb.append(", validatorName='").append(validatorName).append('\'');
        sb.append(", validationResult='").append(validationResult).append('\'');
        sb.append(", errorMessage='").append(validationResult.getMessage()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
