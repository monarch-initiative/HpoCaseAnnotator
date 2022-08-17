package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.util.CheckBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.util.TermIdTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class DiseaseTable extends VBox {

    @FXML
    private StackPane contentPane;
    @FXML
    private TableView<ObservableDiseaseStatus> diseases;
    @FXML
    private TableColumn<ObservableDiseaseStatus, Boolean> isPresentColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, TermId> idColumn;
    @FXML
    private TableColumn<ObservableDiseaseStatus, String> labelColumn;
    @FXML
    private Button removeDiseaseStatus;

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

        // TODO - checkbox state is not working
        isPresentColumn.setCellValueFactory(cdf -> cdf.getValue().excludedProperty());
        isPresentColumn.setCellFactory(col -> new CheckBoxTableCell<>());
        idColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "id"));
        idColumn.setCellFactory(column -> new TermIdTableCell<>());
        labelColumn.setCellValueFactory(cdf -> select(cdf.getValue(), "diseaseId", "diseaseName"));

        removeDiseaseStatus.disableProperty().bind(diseases.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void diseaseTableMouseEntered(MouseEvent e) {
        removeDiseaseStatus.setVisible(true);
        e.consume();
    }

    @FXML
    private void diseaseTableMouseExited(MouseEvent e) {
        removeDiseaseStatus.setVisible(false);
        e.consume();
    }

    @FXML
    private void removeDiseaseAction(ActionEvent e) {
        diseases.getItems().remove(diseases.getSelectionModel().getSelectedIndex());
        e.consume();
    }

    public ObservableList<ObservableDiseaseStatus> getItems() {
        return diseases.getItems();
    }

    public ObjectProperty<ObservableList<ObservableDiseaseStatus>> itemsProperty() {
        return diseases.itemsProperty();
    }

    public TableView.TableViewSelectionModel<ObservableDiseaseStatus> getSelectionModel() {
        return diseases.getSelectionModel();
    }
}
