package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

public class PhenotypicFeature extends BaseBindingObservableDataController<ObservablePhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeature.class);
    private final Function<TermId, Term> termSource;
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

    public PhenotypicFeature(Function<TermId, Term> termSource) {
        this.termSource = Objects.requireNonNull(termSource);
    }

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
        onsetComponent.dataProperty().bind(feature.onsetProperty());
        resolutionComponent.dataProperty().bind(feature.resolutionProperty());
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature feature) {
        // term id & label
        termIdLabel.setText(null);
        nameLabel.setText(null);
        termDefinitionLabel.setText(null);
    }

    private void editOnset(ActionEvent event) {
        // TODO - does this work?
//        Dialog<Boolean> dialog = new Dialog<>();
//        dialog.titleProperty().bind(concat("Individual ID: ", nullableStringProperty(item, "id")));
//        dialog.setHeaderText("Edit phenotypic feature");
//        TimeElementEditableComponent edit = new TimeElementEditableComponent();
//        edit.setInitialData(item.get().getOnset());

//        dialog.getDialogPane().setContent(edit);
//        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
//        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
//
//        dialog.showAndWait()
//                .ifPresent(shouldUpdate -> {if (shouldUpdate) edit.commit();});

        event.consume();
    }

    private String getLabelForTerm(TermId termId) {
        Term term = termSource.apply(termId);
        if (term == null)
            return "N/A";
        return term.getName();
    }

    private String getDefinitionForTermId(TermId termId) {
        Term term = termSource.apply(termId);
        if (term == null)
            return "N/A";
        return term.getDefinition();
    }

}
