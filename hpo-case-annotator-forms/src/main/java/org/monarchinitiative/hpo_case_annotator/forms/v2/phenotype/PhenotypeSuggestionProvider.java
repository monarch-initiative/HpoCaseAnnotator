package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class PhenotypeSuggestionProvider implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>> {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    private final Map<String, TermId> nameToTermId = new HashMap<>();

    public PhenotypeSuggestionProvider() {
        ontology.addListener((obs, old, novel) -> {
            if (novel == null)
                nameToTermId.clear();
            else {
                for (Term term : novel.getTerms()) {
                    if (!term.isObsolete()) {
                        TermId id = term.getId();
                        String name = term.getName();
                        nameToTermId.put(name, id);
                    }
                }
            }
        });
    }

    @Override
    public Collection<String > call(AutoCompletionBinding.ISuggestionRequest request) {
        return nameToTermId.keySet().stream()
                .filter(termId -> termId.contains(request.getUserText()))
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

    public Optional<TermId> termIdForName(String termName) {
        return Optional.ofNullable(nameToTermId.get(termName));
    }
}
