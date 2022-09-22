package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClass;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BrowseHpo extends VBox {

    private static final int AUTOCOMPLETION_ROW_COUNT = 14;
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    // This is an optional property, and does not need to be set everywhere BrowseHpo is used
    private final ObjectProperty<Supplier<ObservableTimeElement>> encounterTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Consumer<ObservablePhenotypicFeature>> phenotypicFeatureConsumer = new SimpleObjectProperty<>();
    private final PhenotypeSuggestionProvider suggestionProvider = new PhenotypeSuggestionProvider();

    @FXML
    private TitledTextField searchField;
    @FXML
    private SimpleOntologyClassTreeView ontologyTreeView;
    @FXML
    private PhenotypicFeatureBinding phenotypicFeature;
    @FXML
    private Button addHpo;

    public BrowseHpo() {
        FXMLLoader loader = new FXMLLoader(BrowseHpo.class.getResource("BrowseHpo.fxml"));
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
        // Set up the search field
        searchField.disableProperty().bind(hpo.isNull());
        suggestionProvider.ontologyProperty().bind(hpo);

        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(searchField.getTitledItem(), suggestionProvider);
        binding.minWidthProperty().bind(searchField.widthProperty());
        binding.setVisibleRowCount(AUTOCOMPLETION_ROW_COUNT);
        binding.setHideOnEscape(true);

        searchField.setOnAction(e -> {
            String ontologyClassLabel = searchField.getText();
            Optional<OntologyClass> oc = suggestionProvider.ontologyClassForLabel(ontologyClassLabel);
            if (oc.isPresent())
                ontologyTreeView.scrollTo(oc.get());
            else
                showPopupForUnknownTermName(ontologyClassLabel);
        });

        // Set up the ontology tree
        ontologyTreeView.ontologyProperty().bind(hpo);
        ontologyTreeView.selectedItemProperty().addListener((obs, old, novel) -> {
            if (novel != null) {
                SimpleOntologyClass oc = novel.getValue();
                ObservablePhenotypicFeature opf = new ObservablePhenotypicFeature();
                opf.setTermId(oc.id());
                opf.setLabel(oc.getLabel());
                Supplier<ObservableTimeElement> supplier = encounterTime.get();
                if (supplier != null) {
                    ObservableTimeElement element = supplier.get();
                    opf.setOnset(element);
                }


                phenotypicFeature.setData(opf);
            }
        });

        // Set up the phenotypic feature pane
        phenotypicFeature.hpoProperty().bind(hpo);

        // Set up the OK button
        addHpo.disableProperty().bind(phenotypicFeatureConsumer.isNull());
    }

    @FXML
    private void addHpoTermAction(ActionEvent e) {
        // The button is disabled when phenotypicFeatureConsumer is null, hence this is NPE safe.
        ObservablePhenotypicFeature data = phenotypicFeature.getData();
        if (data != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Add HPO term");
            alert.setHeaderText("%s added".formatted(data.getLabel()));
            alert.showAndWait();

            phenotypicFeatureConsumer.get()
                    .accept(data);
            searchField.getTitledItem().clear();
        }

        e.consume();
    }

    private static void showPopupForUnknownTermName(String ontologyClassLabel) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Sorry");
        alert.setHeaderText("Unknown HPO term label");
        alert.setContentText("The %s was not found".formatted(ontologyClassLabel));
        alert.showAndWait();
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


    public ObjectProperty<Supplier<ObservableTimeElement>> encounterTimeProperty() {
        return encounterTime;
    }

    public void setEncounterTime(Supplier<ObservableTimeElement> encounterTime) {
        this.encounterTime.set(encounterTime);
    }

    public Consumer<ObservablePhenotypicFeature> getPhenotypicFeatureConsumer() {
        return phenotypicFeatureConsumer.get();
    }

    public ObjectProperty<Consumer<ObservablePhenotypicFeature>> phenotypicFeatureConsumerProperty() {
        return phenotypicFeatureConsumer;
    }

    public void setPhenotypicFeatureConsumer(Consumer<ObservablePhenotypicFeature> phenotypicFeatureConsumer) {
        this.phenotypicFeatureConsumer.set(phenotypicFeatureConsumer);
    }
}

