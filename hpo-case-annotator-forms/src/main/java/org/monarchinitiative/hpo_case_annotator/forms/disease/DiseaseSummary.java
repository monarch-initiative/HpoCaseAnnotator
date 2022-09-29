package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableDiseaseStatus;

import java.io.IOException;

public class DiseaseSummary extends VBox {

    @FXML
    private Button removeDiseaseStatus;
    @FXML
    private DiseaseTable diseaseTable;

    public DiseaseSummary() {
        FXMLLoader loader = new FXMLLoader(DiseaseSummary.class.getResource("DiseaseSummary.fxml"));
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
        removeDiseaseStatus.disableProperty().bind(diseaseTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void removeDiseaseAction(ActionEvent e) {
        diseaseTable.getDiseaseStates().remove(diseaseTable.getSelectionModel().getSelectedIndex());
        e.consume();
    }

    public ObservableList<ObservableDiseaseStatus> getDiseaseStates() {
        return diseaseTable.getDiseaseStates();
    }

    public ObjectProperty<ObservableList<ObservableDiseaseStatus>> diseaseStatesProperty() {
        return diseaseTable.diseaseStatesProperty();
    }

    public TableView.TableViewSelectionModel<ObservableDiseaseStatus> getSelectionModel() {
        return diseaseTable.getSelectionModel();
    }
}
