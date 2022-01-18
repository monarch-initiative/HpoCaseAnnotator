package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
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
    private final TextFormatter<Integer> onsetYearsTextFormatter = Formats.integerFormatter();
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
    private final TextFormatter<Integer> resolutionYearsTextFormatter = Formats.integerFormatter();
    @FXML
    private ComboBox<Integer> resolutionMonthsComboBox;
    @FXML
    private ComboBox<Integer> resolutionDaysComboBox;
    @FXML
    private RadioButton resolutionPresentAgeRadioButton;
    @FXML
    private RadioButton resolutionDeathRadioButton;

    private BooleanBinding phenotypicFeatureIsExcluded;
    private ObjectBinding<ObservableAge> onsetBinding;
    private ObjectBinding<ObservableAge> resolutionBinding;

    @FXML
    protected void initialize() {
        initializeToggles();
        initializeAgeFields();
        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();
        onsetBinding = prepareOnsetBinding();
        resolutionBinding = prepareResolutionBinding();
    }

    private ObjectBinding<ObservableAge> prepareOnsetBinding() {
        return Bindings.createObjectBinding(() -> {
                    RadioButton selectedToggle = (RadioButton) onsetToggleGroup.getSelectedToggle();
                    if (selectedToggle == null)
                        return null;
                    if (selectedToggle.equals(onsetAgeRadioButton)) {
                        // age
                        return extractAge(onsetYearsTextFormatter, onsetMonthsComboBox, onsetDaysComboBox);
                    } else if (selectedToggle.equals(onsetBirthRadioButton)) {
                        // birth
                        return new ObservableAge();
                    } else {
                        LOGGER.warn("Neither age nor birth selected in the onset field");
                        return null;
                    }
                },
                onsetToggleGroup.selectedToggleProperty(), onsetYearsTextField.textProperty(), onsetMonthsComboBox.valueProperty(), onsetDaysComboBox.valueProperty());

    }

    private static ObservableAge extractAge(TextFormatter<Integer> yearsTextFormatter, ComboBox<Integer> resolutionMonthsComboBox, ComboBox<Integer> resolutionDaysComboBox) {
        try {
            int years = yearsTextFormatter.getValue();
            int months = resolutionMonthsComboBox.getValue();
            int days = resolutionDaysComboBox.getValue();
            return new ObservableAge(years, months, days);
        } catch (NumberFormatException e) {
            LOGGER.warn("Error in onset age format {}", e.getMessage());
            return null;
        }
    }

    private ObjectBinding<ObservableAge> prepareResolutionBinding() {
        return Bindings.createObjectBinding(() -> {
            RadioButton selectedToggle = (RadioButton) resolutionToggleGroup.getSelectedToggle();
            if (selectedToggle == null)
                return null;
            if (selectedToggle.equals(resolutionAgeRadioButton)) {
                // age
                return extractAge(resolutionYearsTextFormatter, resolutionMonthsComboBox, resolutionDaysComboBox);
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
        onsetYearsTextField.setTextFormatter(onsetYearsTextFormatter);
        onsetMonthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        onsetMonthsComboBox.getSelectionModel().selectFirst();
        onsetDaysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        onsetDaysComboBox.getSelectionModel().selectFirst();

        resolutionYearsTextField.setTextFormatter(resolutionYearsTextFormatter);
        resolutionMonthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        resolutionMonthsComboBox.getSelectionModel().selectFirst();
        resolutionDaysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        resolutionDaysComboBox.getSelectionModel().selectFirst();
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

        // onset
        ObservableAge onset = feature.getObservationAge().getOnset();
        if (onset != null) {
            if (onset.getYears() == 0 && onset.getMonths() == 0 && onset.getDays() == 0)
                onsetToggleGroup.selectToggle(onsetBirthRadioButton);
            else {
                onsetToggleGroup.selectToggle(onsetAgeRadioButton);
                onsetYearsTextFormatter.setValue(onset.getYears());
                onsetMonthsComboBox.getSelectionModel().select(onset.getMonths());
                onsetDaysComboBox.getSelectionModel().select(onset.getDays());
            }
        }
        feature.getObservationAge().onsetProperty().bind(onsetBinding);

        // resolution
        ObservableAge resolution = feature.getObservationAge().getResolution();
        if (resolution != null) {
            resolutionToggleGroup.selectToggle(resolutionAgeRadioButton);
            resolutionYearsTextFormatter.setValue(resolution.getYears());
            resolutionMonthsComboBox.getSelectionModel().select(resolution.getMonths());
            resolutionDaysComboBox.getSelectionModel().select(resolution.getDays());
//        if (Period.of(80, 0, 0).equals(resolution)) {
//            resolutionToggleGroup.selectToggle(resolutionDeathRadioButton);
//        } else {

//        }
        }
        feature.getObservationAge().resolutionProperty().bind(resolutionBinding);
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
        feature.getObservationAge().onsetProperty().unbind();
        feature.getObservationAge().resolutionProperty().unbind();
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
