package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

import static javafx.beans.binding.Bindings.select;
import static javafx.beans.binding.Bindings.when;

public class PhenotypeView implements DataEditController<ObservablePedigreeMember> {

    private ObservablePedigreeMember item;

    @FXML
    private IndividualIdsComponent individualIds;
    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypeTerms;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, TermId> termIdColumn;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> termLabel;
    @FXML
    private Parent phenotypicFeature;
    @FXML
    private PhenotypicFeature phenotypicFeatureController;

    @FXML
    private void initialize() {
        termIdColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        termLabel.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLabel()));

        phenotypeTerms.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        phenotypicFeatureController.dataProperty().bind(phenotypeTerms.getSelectionModel().selectedItemProperty());
        phenotypicFeature.disableProperty().bind(phenotypeTerms.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void setInitialData(ObservablePedigreeMember data) {
        item = Objects.requireNonNull(data);

        individualIds.individualIdProperty().bind(item.idProperty());
        individualIds.paternalIdProperty().bind(item.paternalIdProperty());
        individualIds.maternalIdProperty().bind(item.maternalIdProperty());
        individualIds.ageProperty().bind(item.ageProperty());
        individualIds.sexProperty().bind(item.sexProperty().asString());
        individualIds.probandProperty().bind(when(item.probandProperty()).then("Yes").otherwise("No"));
        individualIds.vitalStatusProperty().bind(select(item.vitalStatusProperty(), "status").asString());
        individualIds.timeOfDeathProperty().bind(select(item.vitalStatusProperty(), "timeOfDeath"));

        phenotypeTerms.getItems().addAll(item.getObservablePhenotypicFeatures());
    }

    @Override
    public void commit() {
        // TODO - implement
    }
}
