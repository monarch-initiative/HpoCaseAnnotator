package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.IndividualStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.stream.Stream;

public class ObservableIndividualStudy extends ObservableStudy implements IndividualStudy {

    public static final Callback<ObservableIndividualStudy, Observable[]> EXTRACTOR = obs -> {
        Observable[] parent = ObservableStudy.EXTRACTOR.call(obs);
        Observable[] current = new Observable[]{obs.individual};

        Observable[] result = new Observable[parent.length + current.length];
        System.arraycopy(parent, 0, result, 0, parent.length);
        System.arraycopy(current, 0, result, parent.length, current.length);

        return result;
    };

    private final ObjectProperty<ObservableIndividual> individual = new SimpleObjectProperty<>(this, "individual");

    public ObservableIndividualStudy() {
    }

    public ObservableIndividualStudy(IndividualStudy individualStudy) {
        super(individualStudy);
        if (individualStudy != null) {
            if (individualStudy.getIndividual() != null)
                individual.set(new ObservableIndividual(individualStudy.getIndividual()));
        }
    }

    @Override
    public ObservableIndividual getIndividual() {
        return individual.get();
    }

    public void setIndividual(ObservableIndividual individual) {
        this.individual.set(individual);
    }

    public ObjectProperty<ObservableIndividual> individualProperty() {
        return individual;
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.concat(
                super.objectProperties(),
                Stream.of(individual)
        );
    }

    @Override
    public String toString() {
        return "ObservableIndividualStudy{" +
                "id=" + getId() +
                ", publication=" + getPublication() +
                ", variants=" + getVariants().stream().map(CuratedVariant::toString).toList() +
                ", individual=" + individual.get() +
                ", studyMetadata=" + getStudyMetadata() +
                '}';
    }
}
