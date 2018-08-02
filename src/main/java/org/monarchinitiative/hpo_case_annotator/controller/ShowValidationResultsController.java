package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.validation.AbstractValidator;
import org.monarchinitiative.hpo_case_annotator.validation.ValidationLine;
import org.monarchinitiative.hpo_case_annotator.validation.ValidationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * This class is the controller for dialog that presents results of validation of {@link DiseaseCaseModel}s. Validation
 * is performed by {@link ValidationRunner}. For each model, the runner creates number of results corresponding to the
 * number of {@link AbstractValidator} subclasses in , therefore for each model there are so many lines as many
 * validators are defined in the runner. <p>The content of table is read-only.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ShowValidationResultsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowValidationResultsController.class);

    @FXML
    private TableView<ValidationLine> validationResultsTableView;

    @FXML
    private TableColumn<ValidationLine, String> modelName;

    @FXML
    private TableColumn<ValidationLine, String> validatorName;

    @FXML
    private TableColumn<ValidationLine, String> validationResult;

    @FXML
    private TableColumn<ValidationLine, String> errorMessage;


    public void initialize() {
        modelName.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getModelName()));
        validatorName.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValidatorName()));
        validationResult.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValidationResult()));
        errorMessage.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getErrorMessage()));
    }


    public void setValidationLines(Collection<ValidationLine> lines) {
        if (validationResultsTableView != null) { // controller was processed by FXMLLoader
            validationResultsTableView.getItems().clear();
            validationResultsTableView.getItems().addAll(lines);
        } else {
            LOGGER.warn("Unable to add validation lines, controller was not processed by FXMLLoader.");
        }

    }
}
