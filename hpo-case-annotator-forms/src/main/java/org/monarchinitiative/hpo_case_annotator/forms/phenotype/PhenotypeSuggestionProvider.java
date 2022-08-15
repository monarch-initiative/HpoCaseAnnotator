package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.*;

public class PhenotypeSuggestionProvider implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>> {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    private final Map<String, Term> labelToId = new HashMap<>();

    public PhenotypeSuggestionProvider() {
        ontology.addListener((obs, old, novel) -> {
            if (novel == null)
                labelToId.clear();
            else {
                for (Term term : novel.getTerms()) {
                    if (!term.isObsolete()) {
                        String name = term.getName();
                        labelToId.put(name, term);
                    }
                }
            }
        });
    }

    @Override
    public Collection<String> call(AutoCompletionBinding.ISuggestionRequest request) {
        return labelToId.keySet().stream()
                .filter(termLabel -> termLabel.contains(request.getUserText()))
                .toList();
    }

    public Ontology getOntology() {
        return ontology.get();
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public Optional<OntologyClass> ontologyClassForLabel(String label) {
        return Optional.ofNullable(labelToId.get(label))
                .map(OntologyClass::of);
    }
}
