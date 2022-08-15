package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;

public class AddClinicalEncounter extends VBoxObservableDataController<BaseObservableIndividual> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();

    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;
    @FXML
    private TimeElementEditableComponent encounterTime;
    @FXML
    private BrowseHpo browseHpo;
    @FXML
    private VBox textMining;
    @FXML
    private TextMiningController textMiningController;
    @FXML
    private PhenotypeTable phenotypeTable;

    public AddClinicalEncounter() {
        FXMLLoader loader = new FXMLLoader(AddClinicalEncounter.class.getResource("AddClinicalEncounter.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    protected void bind(BaseObservableIndividual data) {
        individualIds.setData(data);
    }

    @Override
    protected void unbind(BaseObservableIndividual data) {
        individualIds.setData(null);
    }

    public Ontology getHpo() {
        return hpo.get();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public void setHpo(Ontology hpo) {
        this.hpo.set(hpo);
    }

    public ObjectProperty<ObservableList<ObservablePhenotypicFeature>> itemsProperty() {
        return phenotypeTable.itemsProperty();
    }
}
