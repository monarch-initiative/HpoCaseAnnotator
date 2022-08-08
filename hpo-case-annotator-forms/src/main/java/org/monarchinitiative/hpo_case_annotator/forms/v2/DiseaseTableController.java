package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;

public class DiseaseTableController {
    @FXML
    private TableView<ObservableDiseaseStatus> diseaseTableView;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> diseaseIdTableColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> diseaseNameTableColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, Boolean> isExcludedTableColumn;

    @FXML
    private void initialize() {
        diseaseIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDiseaseId().id().getValue()));
        diseaseNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getDiseaseId().getDiseaseName()));
        isExcludedTableColumn.setCellValueFactory(cdf -> cdf.getValue().excludedProperty());
        isExcludedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isExcludedTableColumn));
    }

    public ObservableList<ObservableDiseaseStatus> diseaseStatuses() {
        return diseaseTableView.getItems();
    }

    public TableView.TableViewSelectionModel<ObservableDiseaseStatus> selectionModel() {
        return diseaseTableView.getSelectionModel();
    }
}
