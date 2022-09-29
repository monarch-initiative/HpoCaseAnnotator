package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.util.TermIdTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class DiseaseTable extends VBox {

    @FXML
    private TableView<ObservableDiseaseStatus> diseases;
    @FXML
    private TableColumn<ObservableDiseaseStatus, Boolean> isExcludedColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, TermId> idColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> labelColumn;

    public DiseaseTable() {
        FXMLLoader loader = new FXMLLoader(DiseaseTable.class.getResource("DiseaseTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        diseases.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        isExcludedColumn.setCellValueFactory(cdf -> cdf.getValue().excludedProperty());
        isExcludedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isExcludedColumn));
        idColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "id"));
        idColumn.setCellFactory(column -> new TermIdTableCell<>());
        labelColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "diseaseName"));
    }

    public ObservableList<ObservableDiseaseStatus> getDiseaseStates() {
        return diseases.getItems();
    }

    public ObjectProperty<ObservableList<ObservableDiseaseStatus>> diseaseStatesProperty() {
        return diseases.itemsProperty();
    }

    public TableView.TableViewSelectionModel<ObservableDiseaseStatus> getSelectionModel() {
        return diseases.getSelectionModel();
    }
}
