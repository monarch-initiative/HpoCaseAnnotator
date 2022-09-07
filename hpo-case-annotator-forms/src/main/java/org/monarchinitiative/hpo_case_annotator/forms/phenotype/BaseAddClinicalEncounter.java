package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementDataEdit;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.net.URL;

public class BaseAddClinicalEncounter<T extends BaseObservableIndividual> extends VBoxBindingObservableDataComponent<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();

    @FXML
    private BaseIndividualIdsComponent<T> individualIds;
    @FXML
    private TimeElementDataEdit encounterTime;
    @FXML
    private BrowseHpo browseHpo;
    @FXML
    private VBox textMining;
    @FXML
    private TextMiningController textMiningController;
    @FXML
    private PhenotypeTable phenotypeTable;

    protected BaseAddClinicalEncounter(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<ObservableList<ObservablePhenotypicFeature>> itemsProperty() {
        return phenotypeTable.itemsProperty();
    }

    @FXML
    protected void initialize() {
        super.initialize();

        // Browse HPO tab
        browseHpo.setPhenotypicFeatureConsumer(phenotypeTable.getItems()::add);
        browseHpo.hpoProperty().bind(hpo);
        ObservableTimeElement ote = new ObservableTimeElement();
        encounterTime.setInitialData(ote);

        // TODO - this is brittle, finish setup!
        browseHpo.setEncounterTime(() -> {
            encounterTime.commit();
            return ote;
        });

        // Text mining tab
        // TODO - setup
//        textMiningController.encounterTimeProperty().bind(encounterTime.dataProperty());
    }

    @Override
    protected void bind(T data) {
        individualIds.setData(data);
    }

    @Override
    protected void unbind(T data) {
        individualIds.setData(null);
    }
}
