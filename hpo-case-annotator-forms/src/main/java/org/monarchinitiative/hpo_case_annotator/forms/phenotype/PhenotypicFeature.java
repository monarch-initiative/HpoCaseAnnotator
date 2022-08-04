package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class PhenotypicFeature extends VBox implements ObservableDataController<ObservablePhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeature.class);

    private final ObjectProperty<ObservablePhenotypicFeature> item = new SimpleObjectProperty<>();
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>();

    private final ToggleGroup presenceStatusToggleGroup = new ToggleGroup();

    @FXML
    private Label termIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label termDefinitionLabel;
    @FXML
    private RadioButton presentRadioButton;
    @FXML
    private RadioButton absentRadioButton;
    @FXML
    private TimeElementComponent onsetComponent;
    @FXML
    private TimeElementComponent resolutionComponent;

    private BooleanBinding phenotypicFeatureIsExcluded;

    public PhenotypicFeature() {
        FXMLLoader loader = new FXMLLoader(PhenotypicFeature.class.getResource("PhenotypicFeature.fxml"));
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
        presentRadioButton.setToggleGroup(presenceStatusToggleGroup);
        absentRadioButton.setToggleGroup(presenceStatusToggleGroup);

        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();

        item.addListener((obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        });
    }

    private BooleanBinding preparePhenotypicFeatureIsExcludedBinding() {
        return Bindings.createBooleanBinding(() -> {
            RadioButton selected = (RadioButton) presenceStatusToggleGroup.getSelectedToggle();
            if (selected.equals(presentRadioButton)) {
                return false;
            } else if (selected.equals(absentRadioButton)) {
                return true;
            } else {
                LOGGER.warn("Presence status toggle group is not set to presentRadioButton nor to absentRadioButton");
                return false;
            }
        }, presenceStatusToggleGroup.selectedToggleProperty());
    }

//    @Override
    protected void bind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(feature.getTermId().getValue());
//        nameLabel.setText(getLabelForTerm(feature.getTermId()));
//        termDefinitionLabel.setText(getDefinitionForTermId(feature.getTermId()));
//
        // status
        if (feature.isExcluded()) {
            presenceStatusToggleGroup.selectToggle(absentRadioButton);
        } else {
            presenceStatusToggleGroup.selectToggle(presentRadioButton);
        }
        feature.excludedProperty().bind(phenotypicFeatureIsExcluded);

        // observation onset & resolution
        onsetComponent.dataProperty().bind(feature.onsetProperty());
        resolutionComponent.dataProperty().bind(feature.resolutionProperty());
    }

    protected void unbind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(null);
        nameLabel.setText(null);
        termDefinitionLabel.setText(null);
    }

    private void editOnset(ActionEvent event) {
        // TODO - does this work?
        Dialog<Boolean> dialog = new Dialog<>();
//        dialog.titleProperty().bind(concat("Individual ID: ", nullableStringProperty(item, "id")));
        dialog.setHeaderText("Edit phenotypic feature");
        TimeElementEditableComponent edit = new TimeElementEditableComponent();
        edit.setInitialData(item.get().getOnset());

        dialog.getDialogPane().setContent(edit);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .filter(i -> i)
                .ifPresent(shouldUpdate -> item.getValue().setOnset(edit.getEditedData()));

        event.consume();
    }

    private String getLabelForTerm(TermId termId) {
        return getOntologyOptional()
                .flatMap(ontology -> getTerm(ontology, termId))
                .map(Term::getName)
                .orElse(null);
    }

    private Optional<Ontology> getOntologyOptional() {
        return Optional.ofNullable(ontology.get());
    }

    private static Optional<Term> getTerm(Ontology ontology, TermId termId) {
        return Optional.ofNullable(ontology.getTermMap().get(termId));
    }

    private String getDefinitionForTermId(TermId termId) {
        return getOntologyOptional()
                .flatMap(ontology -> getTerm(ontology, termId))
                .map(Term::getDefinition)
                .orElse(null);
    }

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    @Override
    public ObjectProperty<ObservablePhenotypicFeature> dataProperty() {
        return item;
    }
}
