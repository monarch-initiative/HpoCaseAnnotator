package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

public class ObservableFamilyStudy extends ObservableStudy implements FamilyStudy, Updateable<FamilyStudy> {

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
    public <U extends FamilyStudy> void update(U data) {
        updateStudy(data);
        if (data == null)
            pedigree.get().update(null);
        else
            pedigree.get().update(data.getPedigree());
    }

    @Override
    public String toString() {
        return "ObservableFamilyStudy{" +
                "id=" + getId() +
                ", publication=" + getPublication() +
                ", variants=" + getVariants() +
                ", pedigree=" + pedigree.get() +
                ", studyMetadata=" + getStudyMetadata() +
                "} ";
    }
}
