package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.SelectableOntologyTreeController;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class OntologyController {

    private final ObjectProperty<PhenotypeDescription> phenotypeDescriptionInFocus = new SimpleObjectProperty<>(this, "phenotypeDescriptionInFocus", null);
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology", null);
    private final Map<String, TermId> nameToTermId = new HashMap<>();
    @FXML
    private TextField searchTextField;
    @FXML
    private VBox ontologyTree;
    @FXML
    private SelectableOntologyTreeController selectableOntologyTreeController;


    public ObjectProperty<PhenotypeDescription> phenotypeDescriptionInFocusProperty() {
        return phenotypeDescriptionInFocus;
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

    @FXML
    private void initialize() {
        selectableOntologyTreeController.phenotypeDescriptionInFocusProperty().bind(phenotypeDescriptionInFocus);
        selectableOntologyTreeController.ontologyProperty().bind(ontology);

        ontology.addListener(updateNameToTermIdMap());
        initializeSearchTextField();
    }

    private ChangeListener<Ontology> updateNameToTermIdMap() {
        return (obs, old, novel) -> {
            if (novel == null)
                nameToTermId.clear();
            else
                novel.getTerms().forEach(term -> nameToTermId.putIfAbsent(term.getName(), term.getId()));
        };
    }

    private void initializeSearchTextField() {
        searchTextField.disableProperty().bind(ontology.isNull());
        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(searchTextField, nameToTermId.keySet());
        binding.prefWidthProperty().bind(searchTextField.widthProperty());
    }

    @FXML
    private void searchTextFieldAction(ActionEvent event) {
        String termName = searchTextField.getText();
        TermId id = nameToTermId.get(termName);
        if (id == null) {
            // TODO: 10/27/21 alert the user that we cannot find this one
            return;
        }
        if (id.equals(phenotypeDescriptionInFocus.get().getTermId()))
            phenotypeDescriptionInFocus.set(null);
//        phenotypeDescriptionInFocus.setValue(id);
        searchTextField.clear();
        event.consume();
    }

}
