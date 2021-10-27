package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.controlsfx.control.SearchableComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormsUtils;

import java.time.Period;
import java.util.Optional;

public class PhenotypicFeatureController {

    private final ToggleGroup statusToggleGroup = new ToggleGroup();
    private final ToggleGroup onsetToggleGroup = new ToggleGroup();
    private final ToggleGroup resolutionToggleGroup = new ToggleGroup();
    @FXML
    private VBox phenotypicFeatureAllControlsVBox;
    @FXML
    private Label termIdLabel;
    @FXML
    private Label nameLabel;
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
    private SearchableComboBox<Integer> onsetMonthsComboBox;
    @FXML
    private SearchableComboBox<Integer> onsetDaysComboBox;
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
    private SearchableComboBox<Integer> resolutionMonthsComboBox;
    @FXML
    private SearchableComboBox<Integer> resolutionDaysComboBox;
    @FXML
    private RadioButton presentAgeRadioButton;
    @FXML
    private RadioButton resolutionDeathRadioButton;

    private static BooleanBinding makeToggleGroupBinding(String radioButtonId, ToggleGroup toggleGroup) {
        return Bindings.createBooleanBinding(() -> {
                    RadioButton selectedToggle = (RadioButton) toggleGroup.getSelectedToggle();
                    return selectedToggle != null && radioButtonId.equals(selectedToggle.getId());
                },
                toggleGroup.selectedToggleProperty()
        ).not();
    }

    public void initialize() {
        initializeToggles();
        initializeAgefields();
    }

    private void initializeToggles() {
        presentRadioButton.setToggleGroup(statusToggleGroup);
        absentRadioButton.setToggleGroup(statusToggleGroup);

        onsetAgeRadioButton.setToggleGroup(onsetToggleGroup);
        onsetPresentAgeRadioButton.setToggleGroup(onsetToggleGroup);
        onsetBirthRadioButton.setToggleGroup(onsetToggleGroup);
        BooleanBinding onsetAgeToggleIsNotSelected = makeToggleGroupBinding(onsetAgeRadioButton.getId(), onsetToggleGroup);
        onsetAgeControlsVBox.disableProperty().bind(onsetAgeToggleIsNotSelected);

        resolutionAgeRadioButton.setToggleGroup(resolutionToggleGroup);
        presentAgeRadioButton.setToggleGroup(resolutionToggleGroup);
        resolutionDeathRadioButton.setToggleGroup(resolutionToggleGroup);
        BooleanBinding resolutionAgeToggleIsNotSelected = makeToggleGroupBinding(resolutionAgeRadioButton.getId(), resolutionToggleGroup);
        resolutionAgeControlsVBox.disableProperty().bind(resolutionAgeToggleIsNotSelected);
    }

    private void initializeAgefields() {
        onsetMonthsComboBox.getItems().addAll(FormsUtils.getIntegers(11));
        onsetMonthsComboBox.getSelectionModel().selectFirst();
        onsetDaysComboBox.getItems().addAll(FormsUtils.getIntegers(30));
        onsetDaysComboBox.getSelectionModel().selectFirst();

        resolutionMonthsComboBox.getItems().addAll(FormsUtils.getIntegers(11));
        resolutionMonthsComboBox.getSelectionModel().selectFirst();
        resolutionDaysComboBox.getItems().addAll(FormsUtils.getIntegers(30));
        resolutionDaysComboBox.getSelectionModel().selectFirst();
    }

    public BooleanProperty disablePhenotypicFeature() {
        return phenotypicFeatureAllControlsVBox.disableProperty();
    }

    public void setPhenotypicFeature(PhenotypeDescription feature) {
        if (feature == null) {
            phenotypicFeatureAllControlsVBox.setDisable(true);
            cleanForm();
        } else {
            phenotypicFeatureAllControlsVBox.setDisable(false);
            termIdLabel.setText(feature.getTermId().getValue());
            nameLabel.setText(feature.getLabel());
            // status
            if (feature.isPresent())
                statusToggleGroup.selectToggle(presentRadioButton);
            else
                statusToggleGroup.selectToggle(absentRadioButton);
            // onset
            Period onset = feature.getOnset();
            if (Period.ZERO.equals(onset))
                onsetToggleGroup.selectToggle(onsetBirthRadioButton);
            else {
                onsetToggleGroup.selectToggle(onsetAgeRadioButton);
                onsetYearsTextField.setText(String.valueOf(onset.getYears()));
                onsetMonthsComboBox.getSelectionModel().select((Integer) onset.getMonths());
                onsetDaysComboBox.getSelectionModel().select((Integer) onset.getDays());
            }
            // resolution
            Period resolution = feature.getResolution();
            if (Period.of(80, 0, 0).equals(resolution)) {
                resolutionToggleGroup.selectToggle(resolutionDeathRadioButton);
            } else {
                resolutionToggleGroup.selectToggle(resolutionAgeRadioButton);
                resolutionYearsTextField.setText(String.valueOf(resolution.getYears()));
                resolutionMonthsComboBox.getSelectionModel().select((Integer) resolution.getMonths());
                resolutionDaysComboBox.getSelectionModel().select((Integer) resolution.getDays());
            }
        }
    }

    private void cleanForm() {
        termIdLabel.setText("");
        nameLabel.setText("");
        statusToggleGroup.selectToggle(null);
        onsetToggleGroup.selectToggle(null);
        onsetYearsTextField.setText("");
        onsetMonthsComboBox.getSelectionModel().selectFirst();
        onsetDaysComboBox.getSelectionModel().selectFirst();
        resolutionToggleGroup.selectToggle(null);
        resolutionYearsTextField.setText("");
        resolutionMonthsComboBox.getSelectionModel().selectFirst();
        resolutionDaysComboBox.getSelectionModel().selectFirst();
    }

    public Optional<PhenotypeDescription> getPhenotypicFeature() {
        // TODO: 10/26/21 implement
        return Optional.empty();
    }

}
