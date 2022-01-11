package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.Optional;

public class PhenotypicFeaturesTableController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeaturesTableController.class);

    @FXML
    private TableView<ObservablePhenotypicFeature> termsTableView;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> termIdTableColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, Period> onsetTableColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, Period> resolutionTableColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, Boolean> presentTableColumn;

    public void initialize() {
        termIdTableColumn.setCellValueFactory(cd -> cd.getValue().termIdProperty().asString());
        onsetTableColumn.setCellValueFactory(cd -> cd.getValue().getObservationAge().onsetProperty());
        onsetTableColumn.setCellFactory(PeriodTableCell.of());
        resolutionTableColumn.setCellValueFactory(cd -> cd.getValue().getObservationAge().resolutionProperty());
        resolutionTableColumn.setCellFactory(PeriodTableCell.of());
        presentTableColumn.setCellValueFactory(cd -> cd.getValue().excludedProperty());
        presentTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(presentTableColumn));

        termsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public ReadOnlyObjectProperty<ObservablePhenotypicFeature> selectedPhenotypeDescription() {
        return termsTableView.getSelectionModel().selectedItemProperty();
    }


    public ObservableList<ObservablePhenotypicFeature> observablePhenotypeDescriptions() {
        return termsTableView.getItems();
    }

    public TableView.TableViewSelectionModel<ObservablePhenotypicFeature> getSelectionModel() {
        return termsTableView.getSelectionModel();
    }

    public ObjectProperty<TableView.TableViewSelectionModel<ObservablePhenotypicFeature>> selectionModelProperty() {
        return termsTableView.selectionModelProperty();
    }

    public void termTableOnMouseClicked(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
            int index = termsTableView.getSelectionModel().getSelectedIndex();
            ObservablePhenotypicFeature observablePhenotypicFeature = termsTableView.getItems().get(index);
            addEditPhenotypicFeature(observablePhenotypicFeature)
                    .ifPresent(edited -> termsTableView.getItems().set(index, edited));
            e.consume();
        }
    }

    private Optional<ObservablePhenotypicFeature> addEditPhenotypicFeature(ObservablePhenotypicFeature feature) {
        LOGGER.info("Editing {}", feature);
        return Optional.empty();
    }
}
