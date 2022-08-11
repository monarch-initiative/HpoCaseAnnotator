package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.Optional;

public class ObservablePedigreeMember extends BaseObservableIndividual implements PedigreeMember {

    public static ObservablePedigreeMember defaultInstance() {
        ObservablePedigreeMember instance = new ObservablePedigreeMember();
        instance.setAge(ObservableTimeElement.defaultInstance());
        instance.setVitalStatus(ObservableVitalStatus.defaultInstance());
        return instance;
    }

    private final StringProperty paternalId = new SimpleStringProperty(this, "paternalId");
    private final StringProperty maternalId = new SimpleStringProperty(this, "maternalId");
    private final BooleanProperty proband = new SimpleBooleanProperty(this, "proband");

    public ObservablePedigreeMember() {
    }

    public ObservablePedigreeMember(PedigreeMember pedigreeMember) {
        super(pedigreeMember);
        if (pedigreeMember != null) {
            paternalId.set(pedigreeMember.getPaternalId().orElse(null));
            maternalId.set(pedigreeMember.getMaternalId().orElse(null));
            proband.set(pedigreeMember.isProband());
        }
    }

    @Override
    public Optional<String> getPaternalId() {
        return Optional.ofNullable(paternalId.get());
    }

    public void setPaternalId(String paternalId) {
        this.paternalId.set(paternalId);
    }

    public StringProperty paternalIdProperty() {
        return paternalId;
    }

    @Override
    public Optional<String> getMaternalId() {
        return Optional.ofNullable(maternalId.get());
    }

    public void setMaternalId(String maternalId) {
        this.maternalId.set(maternalId);
    }

    public StringProperty maternalIdProperty() {
        return maternalId;
    }

    @Override
    public boolean isProband() {
        return proband.get();
    }

    public void setProband(boolean proband) {
        this.proband.set(proband);
    }

    public BooleanProperty probandProperty() {
        return proband;
    }

    @Override
    public String toString() {
        return "ObservablePedigreeMember{" +
                "id=" + getId() +
                ", paternalId=" + paternalId.get() +
                ", maternalId=" + maternalId.get() +
                ", proband=" + proband.get() +
                ", age=" + getAge() +
                ", vitalStatus=" + getVitalStatus() +
                ", sex=" + getSex() +
                ", phenotypicFeatures=" + getPhenotypicFeatures().stream().map(PhenotypicFeature::toString).toList() +
                ", diseaseStates=" + getDiseaseStates().stream().map(DiseaseStatus::toString).toList() +
                ", genotypes=" + getGenotypes().stream().map(VariantGenotype::toString).toList() +
                "}";
    }

}
