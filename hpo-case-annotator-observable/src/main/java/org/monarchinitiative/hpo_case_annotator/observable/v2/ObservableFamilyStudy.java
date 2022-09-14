package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.stream.Stream;

public class ObservableFamilyStudy extends ObservableStudy implements FamilyStudy {

    public static final Callback<ObservableFamilyStudy, Observable[]> EXTRACTOR = obs -> {
        Observable[] parent = ObservableStudy.EXTRACTOR.call(obs);
        Observable[] current = new Observable[]{obs.pedigree};

        Observable[] result = new Observable[parent.length + current.length];
        System.arraycopy(parent, 0, result, 0, parent.length);
        System.arraycopy(current, 0, result, parent.length, current.length);

        return result;
    };

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
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.concat(
                super.objectProperties(),
                Stream.of(pedigree)
        );
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
