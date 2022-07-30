package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class PhenotypeViewController implements DataEditController<ObservablePedigreeMember> {

    private ObservablePedigreeMember item;

    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypeTerms;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, TermId> termIdColumn;
    @FXML
    private PhenotypicFeature phenotypicFeature;


    @FXML
    private void initialize() {
        phenotypeTerms.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        termIdColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));

        phenotypicFeature.dataProperty().bind(phenotypeTerms.getSelectionModel().selectedItemProperty());
    }

    @Override
    public void setInitialData(ObservablePedigreeMember data) {
        if (data == null) {
            item = new ObservablePedigreeMember();
        } else {
            phenotypeTerms.getItems().addAll(data.getObservablePhenotypicFeatures());
        }
        item = data;
    }

    @Override
    public ObservablePedigreeMember getEditedData() {


        return item;
    }
}
