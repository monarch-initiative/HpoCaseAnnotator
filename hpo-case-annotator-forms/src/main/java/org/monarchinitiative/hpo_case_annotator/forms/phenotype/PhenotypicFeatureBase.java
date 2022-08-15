package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PhenotypicFeatureBase extends VBoxObservableDataController<ObservablePhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeatureBase.class);

    protected static final String NOT_AVAILABLE = "N/A";
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ToggleGroup presenceStatusToggleGroup = new ToggleGroup();

    private BooleanBinding phenotypicFeatureIsExcluded;
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
    protected void initialize() {
        super.initialize();

        presentRadioButton.setToggleGroup(presenceStatusToggleGroup);
        absentRadioButton.setToggleGroup(presenceStatusToggleGroup);

        phenotypicFeatureIsExcluded = preparePhenotypicFeatureIsExcludedBinding();
    }

    @Override
    protected void bind(ObservablePhenotypicFeature data) {
        // term id & label
        termId.setText(data.getTermId().getValue());
        name.setText(data.getLabel());
        definition.setText(getDefinitionForTermId(data.getTermId()));

        // status
        if (data.isExcluded()) {
            presenceStatusToggleGroup.selectToggle(absentRadioButton);
        } else {
            presenceStatusToggleGroup.selectToggle(presentRadioButton);
        }
        data.excludedProperty().bind(phenotypicFeatureIsExcluded);
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature data) {
        // term id & label
        termId.setText(null);
        name.setText(null);
        definition.setText(null);
        data.excludedProperty().unbind();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
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


    private String getDefinitionForTermId(TermId termId) {
        Ontology ontology = hpo.get();
        if (ontology == null)
            return "HPO is missing";

        Term term = ontology.getTermMap().get(termId);
        return term == null
                ? NOT_AVAILABLE
                : term.getDefinition();
    }
}
