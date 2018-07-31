package org.monarchinitiative.hpo_case_annotator.util.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.monarchinitiative.hpo_case_annotator.validation.ValidationLine;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Deprecated
public class ValidationResultsController extends PopUpWindow implements Initializable {

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

    @FXML
    private Button closeButton;


    @FXML
    void closeButtonClicked(ActionEvent event) {
        window.close();
    }


    public void addValidationResults(List<ValidationLine> lineList) {
        validationResultsTableView.getItems().addAll(lineList);
    }


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        modelName.setCellValueFactory(new PropertyValueFactory<ValidationLine, String>("modelName"));
        validatorName.setCellValueFactory(new PropertyValueFactory<ValidationLine, String>("validatorName"));
        validationResult.setCellValueFactory(new PropertyValueFactory<ValidationLine, String>("validationResult"));
        errorMessage.setCellValueFactory(new PropertyValueFactory<ValidationLine, String>("errorMessage"));

    }

}
