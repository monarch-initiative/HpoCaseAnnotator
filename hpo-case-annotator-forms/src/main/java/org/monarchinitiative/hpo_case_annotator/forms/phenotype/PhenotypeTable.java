package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import org.monarchinitiative.hpo_case_annotator.forms.component.age.ObservableTimeElementTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.util.TermIdTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

import static javafx.beans.binding.Bindings.when;

public class PhenotypeTable extends VBox {

    @FXML
    private StackPane contentPane;
    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypes;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, TermId> idColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> labelColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> statusColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, ObservableTimeElement> onsetColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, ObservableTimeElement> resolutionColumn;
    @FXML
    private Button removePhenotypicFeature;
//    @FXML
//    private TableColumn<ObservablePhenotypicFeature, String> modifiersColumn;


    public PhenotypeTable() {
        FXMLLoader loader = new FXMLLoader(PhenotypeTable.class.getResource("PhenotypeTable.fxml"));
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
        phenotypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Phenotypes table view
        idColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        idColumn.setCellFactory(column -> new TermIdTableCell<>());
        labelColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLabel()));
        statusColumn.setCellValueFactory(cdf -> when(cdf.getValue().excludedProperty()).then("Excluded").otherwise("Present"));
        onsetColumn.setCellFactory(tc -> new ObservableTimeElementTableCell<>());
        onsetColumn.setCellValueFactory(cdf -> cdf.getValue().onsetProperty());
        resolutionColumn.setCellFactory(tc -> new ObservableTimeElementTableCell<>());
        resolutionColumn.setCellValueFactory(cdf -> cdf.getValue().resolutionProperty());

        removePhenotypicFeature.disableProperty().bind(phenotypes.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void phenotypeTableMouseEntered(MouseEvent e) {
        removePhenotypicFeature.setVisible(true);
        e.consume();
    }

    @FXML
    private void phenotypeTableMouseExited(MouseEvent e) {
        removePhenotypicFeature.setVisible(false);
        e.consume();
    }

    @FXML
    private void removePhenotypicFeatureAction(ActionEvent e) {
        int idx = phenotypes.getSelectionModel().getSelectedIndex();
        phenotypes.getItems().remove(idx);
        e.consume();
    }

    public ObservableList<ObservablePhenotypicFeature> getItems() {
        return phenotypes.getItems();
    }

    public ObjectProperty<ObservableList<ObservablePhenotypicFeature>> itemsProperty() {
        return phenotypes.itemsProperty();
    }

    public TableView.TableViewSelectionModel<ObservablePhenotypicFeature> getSelectionModel() {
        return phenotypes.getSelectionModel();
    }
}
