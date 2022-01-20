package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ObservableFamilyStudy extends ObservableStudy {

    private final ObjectProperty<ObservablePedigree> pedigree = new SimpleObjectProperty<>(this, "pedigree", new ObservablePedigree());

    public ObservableFamilyStudy() {
    }

    public ObservablePedigree getPedigree() {
        return pedigree.get();
    }

    public void setPedigree(ObservablePedigree pedigree) {
        this.pedigree.set(pedigree);
    }

    public ObjectProperty<ObservablePedigree> pedigreeProperty() {
        return pedigree;
    }
}
