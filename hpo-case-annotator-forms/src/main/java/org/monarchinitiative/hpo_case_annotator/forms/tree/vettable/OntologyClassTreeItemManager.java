package org.monarchinitiative.hpo_case_annotator.forms.tree.vettable;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.*;
import org.monarchinitiative.hpo_case_annotator.forms.tree.VettableOntologyClass;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class OntologyClassTreeItemManager implements Observable {

    private final MapProperty<TermId, VettableOntologyClass> ontoItems = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ObservableList<InvalidationListener> listeners = FXCollections.observableArrayList();

    public OntologyClassTreeItemManager() {
        // Add/remove listeners to new/removed ontoItems.
        ontoItems.addListener(addOrRemoveListenersToNewOrRemovedOntoItems());

        // Add/remove new listener to/from all ontoItems
        listeners.addListener(addRemoveNewListenerToFromAllOntoItems());
    }

    private MapChangeListener<? super TermId, ? super VettableOntologyClass> addOrRemoveListenersToNewOrRemovedOntoItems() {
        return change -> {
            if (change.wasAdded())
                listeners.forEach(listener -> change.getValueAdded().addListener(listener));
            if (change.wasRemoved())
                listeners.forEach(listener -> change.getValueRemoved().removeListener(listener));
        };
    }

    private ListChangeListener<? super InvalidationListener> addRemoveNewListenerToFromAllOntoItems() {
        return change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (InvalidationListener listener : change.getAddedSubList()) {
                        for (VettableOntologyClass item : ontoItems.values()) {
                            item.addListener(listener);
                        }
                    }
                } else if (change.wasRemoved()) {
                    for (InvalidationListener listener : change.getRemoved()) {
                        for (VettableOntologyClass item : ontoItems.values()) {
                            item.removeListener(listener);
                        }
                    }
                }
                // We don't care for wasReplaced or wasUpdated. The latter should not be possible as it is impossible
                // to update an InvalidationListener.
            }
        };
    }

    public VettableOntologyClass getOntologyClassForTerm(Term term) {
        return ontoItems.computeIfAbsent(term.id(), id -> new VettableOntologyClass(term.id(), term.getName()));
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    public MapProperty<TermId, VettableOntologyClass> ontologyClassesProperty() {
        return ontoItems;
    }

}
