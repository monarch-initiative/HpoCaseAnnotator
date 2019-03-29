package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is the controller for dialog that presents results of validation of {@link DiseaseCaseModel}s. Validation
 * is performed by {@link ValidationRunner}. For each model, the runner runs a validation using {@link org.monarchinitiative.hpo_case_annotator.core.validation.Validator}s
 * to create a list of {@link ValidationResult}s.
 * <p>
 * The {@link ValidationResult}s are then displayed in the table, each result corresponds to a single table row.
 * <p>
 * The content of table is read-only.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ShowValidationResultsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowValidationResultsController.class);

    private static final String OK_STYLE = "-fx-background-image: url('/img/ok-icon.png'); -fx-background-position: center; -fx-background-insets: 3px;";

    private static final String FAIL_STYLE = "-fx-background-image: url('/img/delete-icon.png'); -fx-background-position: center; -fx-background-insets: 3px;";


    private final Map<DiseaseCase, List<ValidationResult>> resultMapCache;

    @FXML
    public Label validationResultsSummaryLabel;

    @FXML
    private TableView<ValidationLine> validationResultsTableView;

    @FXML
    private TableColumn<ValidationLine, Image> validationResultStatusImage;

    @FXML
    private TableColumn<ValidationLine, String> modelNameTableColumn;

    @FXML
    private TableColumn<ValidationLine, String> messageTableColumn;

    ShowValidationResultsController() {
        resultMapCache = new HashMap<>();
    }

    public void initialize() {
        modelNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getModelName()));
        validationResultStatusImage.setCellFactory(tcol -> new TableCell<ValidationLine, Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    final ValidationLine line = validationResultsTableView.getItems().get(getIndex());
                    if (line.validationResult.isValid()) {
                        setStyle(OK_STYLE);
                    } else {
                        setStyle(FAIL_STYLE);
                    }
                }
            }
        });
        messageTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getMessage()));
        modelNameTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        validationResultsTableView.getSortOrder().add(modelNameTableColumn);

        if (!resultMapCache.isEmpty()) {
            setValidationResults(resultMapCache);
        }
    }

    void setValidationResults(Map<DiseaseCase, List<ValidationResult>> resultMap) {
        if (validationResultsTableView == null) { // the has not yet been processed by FXMLLoader, store results for later
            for (Map.Entry<DiseaseCase, List<ValidationResult>> entry : resultMap.entrySet()) {
                resultMapCache.put(entry.getKey(), entry.getValue());
            }
        } else {
            for (Map.Entry<DiseaseCase, List<ValidationResult>> entry : resultMap.entrySet()) {
                setValidationResult(entry.getKey(), entry.getValue());
            }
        }
    }

    void setValidationResult(DiseaseCase diseaseCase, List<ValidationResult> result) {
        for (ValidationResult vr : result) {
            ValidationLine line = new ValidationLine(ModelUtils.getFileNameFor(diseaseCase), vr);
            validationResultsTableView.getItems().add(line);
        }

        if (validationResultsTableView.getItems().isEmpty()) {
            Platform.runLater(() -> validationResultsSummaryLabel.setText("No issues were found"));
        } else {
            Platform.runLater(() -> validationResultsSummaryLabel.setText("Following issues were identified"));
        }
    }

    /**
     * This POJO contains data that are being presented as lines in {@link TableView} displayed to user
     * after clicking Validate/Validate all XML files of Validate/Validate current model Menu entries.
     */
    public static class ValidationLine {

        /* Disease case model name - First author & year */
        private final String modelName;

        /* Value of ValidationResult enum */
        private final ValidationResult validationResult;

        public ValidationLine(String modelName, ValidationResult result) {
            this.modelName = modelName;
            this.validationResult = result;
        }


        public String getModelName() {
            return modelName;
        }


        public String getMessage() {
            return validationResult.getMessage();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidationLine line = (ValidationLine) o;
            return Objects.equals(modelName, line.modelName) &&
                    Objects.equals(validationResult, line.validationResult);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modelName, validationResult);
        }

        @Override
        public String toString() {
            return "ValidationLine{" +
                    "modelName='" + modelName + '\'' +
                    ", validationResult=" + validationResult +
                    '}';
        }
    }
}
