package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * <h2>Properties</h2>
 * {@link BasePhenotypeDataEdit} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #hpoProperty()}</li>
 * </ul>
 */
public abstract class BasePhenotypeDataEdit<T extends BaseObservableIndividual> extends VBoxDataEdit<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ListProperty<ObservablePhenotypicFeature> phenotypicFeatures = new SimpleListProperty<>(FXCollections.observableArrayList());
    private T item;
    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;
    @FXML
    private Button browseHpo;
    @FXML
    private Button addClinicalEncounter;
    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private PhenotypicFeatureBinding phenotypicFeature;

    protected BasePhenotypeDataEdit(URL location) {
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

    @FXML
    protected void initialize() {
        phenotypicFeature.hpoProperty().bind(hpo);
        phenotypicFeature.dataProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty());
        phenotypicFeature.disableProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty().isNull());
        phenotypeTable.phenotypicFeaturesProperty().bind(phenotypicFeatures);
    }

    @Override
    public void setInitialData(T data) {
        item = Objects.requireNonNull(data);

        individualIds.setData(data);

        for (ObservablePhenotypicFeature feature : item.getPhenotypicFeatures())
            phenotypicFeatures.add(new ObservablePhenotypicFeature(feature));
    }

    @Override
    public void commit() {
        item.phenotypicFeaturesProperty().setAll(phenotypicFeatures);
    }

    @FXML
    private void browseHpoAction(ActionEvent e) {
        BrowseHpo component = new BrowseHpo();
        component.hpoProperty().bind(hpo);
        component.setPhenotypicFeatureConsumer(phenotypeTable.getPhenotypicFeatures()::add);

        showComponentNodeDialog(component);

        e.consume();
    }

    @FXML
    private void addClinicalEncounterAction(ActionEvent e) {
        BaseAddClinicalEncounter<T> component = clinicalEncounterComponent();
        component.hpoProperty().bind(hpo);
        component.setData(item);

        showComponentNodeDialog(component);
        phenotypeTable.getPhenotypicFeatures().addAll(component.itemsProperty().get());

        e.consume();
    }

    protected abstract BaseAddClinicalEncounter<T> clinicalEncounterComponent();

    private static void showComponentNodeDialog(Node component) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }
}
