package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;

import java.io.IOException;
import java.util.Objects;

import static javafx.beans.binding.Bindings.select;

public class PhenotypeDataEdit extends VBox implements DataEditController<BaseObservableIndividual> {

    private BaseObservableIndividual item;

    @FXML
    private IndividualIdsComponent individualIds;
    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private Parent phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;

    public PhenotypeDataEdit(HCAControllerFactory controllerFactory) {
        FXMLLoader loader = new FXMLLoader(PhenotypeDataEdit.class.getResource("PhenotypeDataEdit.fxml"));
        loader.setRoot(this);
        loader.setControllerFactory(controllerFactory);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        phenotypicFeatureController.dataProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty());
        phenotypicFeature.disableProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void setInitialData(BaseObservableIndividual data) {
        item = Objects.requireNonNull(data);

        individualIds.individualIdProperty().bind(item.idProperty());
        individualIds.ageProperty().bind(item.ageProperty());
        individualIds.sexProperty().bind(item.sexProperty().asString());
        individualIds.vitalStatusProperty().bind(select(item.vitalStatusProperty(), "status").asString());
        individualIds.timeOfDeathProperty().bind(select(item.vitalStatusProperty(), "timeOfDeath"));

        phenotypeTable.getItems().addAll(item.getObservablePhenotypicFeatures());
    }

    @Override
    public void commit() {
        // TODO - implement
    }
}
