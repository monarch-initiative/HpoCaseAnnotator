package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.Optional;

public class PhenotypicFeatureController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeatureController.class);

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    private final ToggleGroup presenceStatusToggleGroup = new ToggleGroup();
    private final ToggleGroup onsetToggleGroup = new ToggleGroup();
    private final ToggleGroup resolutionToggleGroup = new ToggleGroup();

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
    private VBox onsetAgeControlsVBox;
    @FXML
    private RadioButton onsetAgeRadioButton;
    @FXML
    private TextField onsetYearsTextField;
    @FXML
    private ComboBox<Integer> onsetMonthsComboBox;
    @FXML
    private ComboBox<Integer> onsetDaysComboBox;
    @FXML
    private RadioButton onsetPresentAgeRadioButton;
    @FXML
    private RadioButton onsetBirthRadioButton;
    @FXML
    private VBox resolutionAgeControlsVBox;
    @FXML
    private RadioButton resolutionAgeRadioButton;
    @FXML
    private TextField resolutionYearsTextField;
    @FXML
    private ComboBox<Integer> resolutionMonthsComboBox;
    @FXML
    private ComboBox<Integer> resolutionDaysComboBox;
    @FXML
    private RadioButton resolutionPresentAgeRadioButton;
    @FXML
    private RadioButton resolutionDeathRadioButton;

    private BooleanBinding phenotypicFeatureIsExcluded;
    private ObjectBinding<Period> onsetBinding;
    private ObjectBinding<Period> resolutionBinding;

    @FXML
    private void initialize() {
        initializeToggles();
        initializeAgeFields();
        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();
        onsetBinding = prepareOnsetBinding();
        resolutionBinding = prepareResolutionBinding();
    }

    private ObjectBinding<Period> prepareOnsetBinding() {
        return Bindings.createObjectBinding(() -> {
                    RadioButton selectedToggle = (RadioButton) onsetToggleGroup.getSelectedToggle();
                    if (selectedToggle.equals(onsetAgeRadioButton)) {
                        // age
                        return extractAge(onsetYearsTextField, onsetMonthsComboBox, onsetDaysComboBox);
                    } else if (selectedToggle.equals(onsetBirthRadioButton)) {
                        // birth
                        return Period.ZERO;
                    } else {
                        LOGGER.warn("Neither age nor birth selected in the onset field");
                        return null;
                    }
                },
                onsetToggleGroup.selectedToggleProperty(), onsetYearsTextField.textProperty(), onsetMonthsComboBox.valueProperty(), onsetDaysComboBox.valueProperty());

    }

    private static Period extractAge(TextField resolutionYearsTextField, ComboBox<Integer> resolutionMonthsComboBox, ComboBox<Integer> resolutionDaysComboBox) {
        try {
            int years = Integer.parseInt(resolutionYearsTextField.getText());
            int months = resolutionMonthsComboBox.getValue();
            int days = resolutionDaysComboBox.getValue();
            return Period.of(years, months, days);
        } catch (NumberFormatException e) {
            LOGGER.warn("Error in onset age format {}", e.getMessage());
            return null;
        }
    }

    private ObjectBinding<Period> prepareResolutionBinding() {
        return Bindings.createObjectBinding(() -> {
            RadioButton selectedToggle = (RadioButton) resolutionToggleGroup.getSelectedToggle();
            if (selectedToggle.equals(resolutionAgeRadioButton)) {
                // age
                return extractAge(resolutionYearsTextField, resolutionMonthsComboBox, resolutionDaysComboBox);
            }
            return null;
        }, resolutionToggleGroup.selectedToggleProperty(), resolutionYearsTextField.textProperty(), resolutionMonthsComboBox.valueProperty(), resolutionDaysComboBox.valueProperty());
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

    private void initializeToggles() {
        initializeStatusToggles();
        initializeAgeToggles();
    }

    private void initializeStatusToggles() {
        presentRadioButton.setToggleGroup(presenceStatusToggleGroup);
        absentRadioButton.setToggleGroup(presenceStatusToggleGroup);
    }

    private void initializeAgeToggles() {
        onsetAgeRadioButton.setToggleGroup(onsetToggleGroup);
//        onsetPresentAgeRadioButton.setToggleGroup(onsetToggleGroup);
        onsetBirthRadioButton.setToggleGroup(onsetToggleGroup);
        BooleanBinding onsetAgeToggleIsNotSelected = makeToggleGroupBinding(onsetAgeRadioButton.getId(), onsetToggleGroup);
        onsetAgeControlsVBox.disableProperty().bind(onsetAgeToggleIsNotSelected);

        resolutionAgeRadioButton.setToggleGroup(resolutionToggleGroup);
//        resolutionPresentAgeRadioButton.setToggleGroup(resolutionToggleGroup);
//        resolutionDeathRadioButton.setToggleGroup(resolutionToggleGroup);
        BooleanBinding resolutionAgeToggleIsNotSelected = makeToggleGroupBinding(resolutionAgeRadioButton.getId(), resolutionToggleGroup);
        resolutionAgeControlsVBox.disableProperty().bind(resolutionAgeToggleIsNotSelected);
    }

    private static BooleanBinding makeToggleGroupBinding(String radioButtonId, ToggleGroup toggleGroup) {
        return Bindings.createBooleanBinding(() -> {
                    RadioButton selectedToggle = (RadioButton) toggleGroup.getSelectedToggle();
                    return selectedToggle != null && radioButtonId.equals(selectedToggle.getId());
                },
                toggleGroup.selectedToggleProperty()
        ).not();
    }

    private void initializeAgeFields() {
        onsetMonthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        onsetMonthsComboBox.getSelectionModel().selectFirst();
        onsetDaysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        onsetDaysComboBox.getSelectionModel().selectFirst();

        resolutionMonthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        resolutionMonthsComboBox.getSelectionModel().selectFirst();
        resolutionDaysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        resolutionDaysComboBox.getSelectionModel().selectFirst();
    }

    public ChangeListener<ObservablePhenotypicFeature> phenotypeDescriptionChangeListener() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

    private void unbind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(null);
        nameLabel.setText(null);
        termDefinitionLabel.setText(null);

        // status
        feature.excludedProperty().unbind();


        // onset & resolution
        feature.getObservationAge().onsetProperty().unbind();
        feature.getObservationAge().resolutionProperty().unbind();
    }

    private void bind(ObservablePhenotypicFeature feature) {
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

        // onset
        Period onset = feature.getObservationAge().getOnset();
        if (Period.ZERO.equals(onset))
            onsetToggleGroup.selectToggle(onsetBirthRadioButton);
        else {
            onsetToggleGroup.selectToggle(onsetAgeRadioButton);
            onsetYearsTextField.setText(String.valueOf(onset.getYears()));
            onsetMonthsComboBox.getSelectionModel().select((Integer) onset.getMonths());
            onsetDaysComboBox.getSelectionModel().select((Integer) onset.getDays());
        }
        feature.getObservationAge().onsetProperty().bind(onsetBinding);

        // resolution
        Period resolution = feature.getObservationAge().getResolution();
//        if (Period.of(80, 0, 0).equals(resolution)) {
//            resolutionToggleGroup.selectToggle(resolutionDeathRadioButton);
//        } else {
        resolutionToggleGroup.selectToggle(resolutionAgeRadioButton);
        resolutionYearsTextField.setText(String.valueOf(resolution.getYears()));
        resolutionMonthsComboBox.getSelectionModel().select((Integer) resolution.getMonths());
        resolutionDaysComboBox.getSelectionModel().select((Integer) resolution.getDays());
//        }
        feature.getObservationAge().resolutionProperty().bind(resolutionBinding);
    }

    private String getLabelForTerm(TermId termId) {
        return getOntologyOptional()
                .flatMap(ontology -> getTerm(ontology, termId))
                .map(Term::getName)
                .orElse(null);
    }

    private String getDefinitionForTermId(TermId termId) {
        return getOntologyOptional()
                .flatMap(ontology -> getTerm(ontology, termId))
                .map(Term::getDefinition)
                .orElse(null);
    }

    private Optional<Ontology> getOntologyOptional() {
        return Optional.ofNullable(ontology.get());
    }

    private static Optional<Term> getTerm(Ontology ontology, TermId termId) {
        return Optional.ofNullable(ontology.getTermMap().get(termId));
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

}
