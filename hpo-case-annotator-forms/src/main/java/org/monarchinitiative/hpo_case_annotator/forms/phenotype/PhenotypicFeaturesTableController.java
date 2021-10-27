package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;

import java.util.List;

public class PhenotypicFeaturesTableController {

    @FXML
    private TableView<PhenotypeDescription> termsTableView;
    @FXML
    private TableColumn<PhenotypeDescription, String> termIdTableColumn;
    @FXML
    private TableColumn<PhenotypeDescription, String> labelTableColumn;
    @FXML
    private TableColumn<PhenotypeDescription, String> onsetTableColumn;
    @FXML
    private TableColumn<PhenotypeDescription, String> resolutionTableColumn;
    @FXML
    private TableColumn<PhenotypeDescription, Boolean> presentTableColumn;
    @FXML
    private Button removeButton;

    public void initialize() {
        initializeTableView();
        initializeRemoveButton();
    }

    private void initializeTableView() {
        termIdTableColumn.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTermId().getValue()));

        labelTableColumn.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getLabel()));

        onsetTableColumn.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getOnset().toString()));

        resolutionTableColumn.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getResolution().toString()));

        presentTableColumn.setCellValueFactory(cd -> new ReadOnlyBooleanWrapper(cd.getValue().isPresent()));
        presentTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(presentTableColumn));

        termsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initializeRemoveButton() {
        TableView.TableViewSelectionModel<PhenotypeDescription> selectionModel = termsTableView.getSelectionModel();
        BooleanBinding nothingIsSelected = Bindings.createBooleanBinding(() -> selectionModel.getSelectedItems().isEmpty(), selectionModel.getSelectedItems());
        removeButton.disableProperty().bind(nothingIsSelected);
    }

    public void addFeatures(PhenotypeDescription... features) {
        termsTableView.getItems().addAll(features);
    }

    public List<PhenotypeDescription> phenotypicFeatures() {
        return List.copyOf(termsTableView.getItems());
    }

    public ReadOnlyObjectProperty<PhenotypeDescription> selectedPhenotypicDescriptionProperty() {
        return termsTableView.getSelectionModel().selectedItemProperty();
    }

    @FXML
    private void removeButtonAction(ActionEvent event) {
        ObservableList<PhenotypeDescription> items = termsTableView.getItems();
        if (!items.isEmpty()) {
            ObservableList<Integer> selectedIndices = termsTableView.getSelectionModel().getSelectedIndices();
            List<PhenotypeDescription> toRemove = selectedIndices.stream()
                    .map(items::get)
                    .toList();

            items.removeAll(toRemove);
        }
        event.consume();
    }
}
