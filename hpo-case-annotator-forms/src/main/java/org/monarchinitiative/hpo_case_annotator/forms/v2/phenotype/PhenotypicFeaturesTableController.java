package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.monarchinitiative.hpo_case_annotator.forms.util.PeriodTableCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

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
    private TableColumn<ObservablePhenotypicFeature, Boolean> excludedTableColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, Period> onsetTableColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, Period> resolutionTableColumn;

    @FXML
    private void initialize() {
        termIdTableColumn.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTermId().getValue()));

        excludedTableColumn.setCellValueFactory(cd -> cd.getValue().excludedProperty());
        excludedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(excludedTableColumn));

        // TODO - implement or discard
//        onsetTableColumn.setCellValueFactory(cd -> cd.getValue().getObservationAge().getOnset().period());
        onsetTableColumn.setCellFactory(PeriodTableCell.of());

        // TODO - implement or discard
//        resolutionTableColumn.setCellValueFactory(cd -> cd.getValue().getObservationAge().getResolution().period());
        resolutionTableColumn.setCellFactory(PeriodTableCell.of());

        termsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    @FXML
    private void termTableOnMouseClicked(MouseEvent e) {
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
