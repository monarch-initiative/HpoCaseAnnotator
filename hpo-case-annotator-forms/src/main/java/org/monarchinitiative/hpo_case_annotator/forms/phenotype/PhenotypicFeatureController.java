package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Objects;
import java.util.function.Function;

@Scope("prototype")
@Controller("nvoPhenotypicFeatureController") // TODO - rename
public class PhenotypicFeatureController extends BaseBindingObservableDataController<ObservablePhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeatureController.class);
    private final Function<TermId, Term> termSource;
    private final ToggleGroup presenceStatusToggleGroup = new ToggleGroup();
    @FXML
    private TitledLabel termId;
    @FXML
    private TitledLabel name;
    @FXML
    private TitledLabel definition;
    @FXML
    private RadioButton presentRadioButton;
    @FXML
    private RadioButton absentRadioButton;
    @FXML
    private Button editTemporalFields;
    @FXML
    private TimeElementComponent onsetComponent;
    @FXML
    private TimeElementComponent resolutionComponent;

    private BooleanBinding phenotypicFeatureIsExcluded;

    public PhenotypicFeatureController(Function<TermId, Term> termSource) {
        this.termSource = Objects.requireNonNull(termSource);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        presentRadioButton.setToggleGroup(presenceStatusToggleGroup);
        absentRadioButton.setToggleGroup(presenceStatusToggleGroup);

        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();

        editTemporalFields.disableProperty().bind(data.isNull());
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
        termId.setText(feature.getTermId().getValue());
        name.setText(feature.getLabel());
        definition.setText(getDefinitionForTermId(feature.getTermId()));

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
        termId.setText(null);
        name.setText(null);
        definition.setText(null);
    }

    private String getDefinitionForTermId(TermId termId) {
        Term term = termSource.apply(termId);
        if (term == null)
            return "N/A";
        return term.getDefinition();
    }

    @FXML
    private void editTemporalFieldsAction(ActionEvent e) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.setTitle("Edit onset and resolution of %s".formatted(data.get().id().getValue()));
        EditTemporalFields etf = new EditTemporalFields();
        etf.setInitialData(data.get());

        dialog.getDialogPane().setContent(etf);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.UPDATE_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        dialog.showAndWait()
                .ifPresent(shouldUpdate -> {if (shouldUpdate) etf.commit();});

        e.consume();
    }
}
