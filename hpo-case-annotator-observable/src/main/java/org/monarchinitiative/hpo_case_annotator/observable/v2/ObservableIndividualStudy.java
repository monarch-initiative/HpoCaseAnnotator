package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.IndividualStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

public class ObservableIndividualStudy extends ObservableStudy implements IndividualStudy {

    public static ObservableIndividualStudy defaultInstance() {
        ObservableIndividualStudy instance = new ObservableIndividualStudy();
        instance.setPublication(ObservablePublication.defaultInstance());
        instance.setIndividual(ObservableIndividual.defaultInstance());
        instance.setStudyMetadata(ObservableStudyMetadata.defaultInstance());
        return instance;
    }

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
