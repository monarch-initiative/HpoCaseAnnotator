package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.AgeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PhenotypicFeatureController extends BindingDataController<ObservablePhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeatureController.class);

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    private final ObjectProperty<ObservablePhenotypicFeature> phenotypicFeature = new SimpleObjectProperty<>(this, "phenotypicFeature");

    private final ToggleGroup presenceStatusToggleGroup = new ToggleGroup();

    @FXML
    private VBox phenotypicFeatureBox;
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
    private VBox onsetAge;
    @FXML
    private AgeController onsetAgeController;
    @FXML
    private VBox resolutionAge;
    @FXML
    private AgeController resolutionAgeController;

    private BooleanBinding phenotypicFeatureIsExcluded;

    @FXML
    protected void initialize() {
        super.initialize();

        presentRadioButton.setToggleGroup(presenceStatusToggleGroup);
        absentRadioButton.setToggleGroup(presenceStatusToggleGroup);

        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();
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

    @Override
    protected void bind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(feature.getTermId().getValue());
        nameLabel.setText(getLabelForTerm(feature.getTermId()));
        termDefinitionLabel.setText(getDefinitionForTermId(feature.getTermId()));

        // status
        if (feature.isExcluded()) {
            presenceStatusToggleGroup.selectToggle(absentRadioButton);
        } else {
            presenceStatusToggleGroup.selectToggle(presentRadioButton);
        }
        feature.excludedProperty().bind(phenotypicFeatureIsExcluded);

        // observation onset & resolution
        ObservableAgeRange ageRange = feature.getObservationAge();
        // onset
        if (ageRange != null) {
        onsetAgeController.dataProperty().bindBidirectional(ageRange.onsetProperty());
        resolutionAgeController.dataProperty().bindBidirectional(ageRange.resolutionProperty());
        }
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(null);
        nameLabel.setText(null);
        termDefinitionLabel.setText(null);

        // status
        feature.excludedProperty().unbind();


        // onset & resolution
        ObservableAgeRange ageRange = feature.getObservationAge();
        if (ageRange != null) {
            onsetAgeController.dataProperty().unbindBidirectional(ageRange.onsetProperty());
            resolutionAgeController.dataProperty().unbindBidirectional(ageRange.resolutionProperty());
        }
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

    public BooleanProperty disableProperty() {
        return phenotypicFeatureBox.disableProperty();
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
        return phenotypicFeature;
    }
}
