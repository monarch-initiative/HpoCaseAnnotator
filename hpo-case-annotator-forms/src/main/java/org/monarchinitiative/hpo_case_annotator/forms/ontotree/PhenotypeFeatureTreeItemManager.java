package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.*;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

class PhenotypeFeatureTreeItemManager implements Observable {

    private final MapProperty<TermId, PhenotypeFeatureItem> ontoItems = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ObservableList<InvalidationListener> listeners = FXCollections.observableArrayList();

    PhenotypeFeatureTreeItemManager() {
        // Add/remove listeners to new/removed ontoItems.
        ontoItems.addListener(addOrRemoveListenersToNewOrRemovedOntoItems());

        // Add/remove new listener to/from all ontoItems
        listeners.addListener(addRemoveNewListenerToFromAllOntoItems());
    }

    private MapChangeListener<? super TermId, ? super PhenotypeFeatureItem> addOrRemoveListenersToNewOrRemovedOntoItems() {
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
                        for (PhenotypeFeatureItem item : ontoItems.values()) {
                            item.addListener(listener);
                        }
                    }
                } else if (change.wasRemoved()) {
                    for (InvalidationListener listener : change.getRemoved()) {
                        for (PhenotypeFeatureItem item : ontoItems.values()) {
                            item.removeListener(listener);
                        }
                    }
                }
                // We don't care for wasReplaced or wasUpdated. The latter should not be possible as it is impossible
                // to update an InvalidationListener.
            }
        };
    }

    PhenotypeFeatureItem getOntoItem(Term term) {
        return ontoItems.computeIfAbsent(term.id(), id -> new PhenotypeFeatureItem(term));
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    MapProperty<TermId, PhenotypeFeatureItem> ontoItemsProperty() {
        return ontoItems;
    }

}
