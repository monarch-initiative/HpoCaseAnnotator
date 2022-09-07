package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

public class ObservableFamilyStudy extends ObservableStudy implements FamilyStudy {

    private final ObjectProperty<ObservablePedigree> pedigree = new SimpleObjectProperty<>(this, "pedigree");

    public ObservableFamilyStudy() {
    }

    public ObservableFamilyStudy(FamilyStudy familyStudy) {
        super(familyStudy);
        if (familyStudy != null) {
            if (familyStudy.getPedigree() != null)
                pedigree.set(new ObservablePedigree(familyStudy.getPedigree()));
        }
    }

    public void setPedigree(ObservablePedigree pedigree) {
        this.pedigree.set(pedigree);
    }

    public ObjectProperty<ObservablePedigree> pedigreeProperty() {
        return pedigree;
    }

    @Override
    public ObservablePedigree getPedigree() {
        return pedigree.get();
    }

    @Override
    public String toString() {
        return "ObservableFamilyStudy{" +
                "id=" + getId() +
                ", publication=" + getPublication() +
                ", variants=" + getVariants().stream().map(CuratedVariant::toString).toList() +
                ", pedigree=" + pedigree.get() +
                ", studyMetadata=" + getStudyMetadata() +
                "} ";
    }
}
