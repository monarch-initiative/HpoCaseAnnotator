package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;
import org.monarchinitiative.hpo_case_annotator.observable.deep.DeepObservable;

import java.util.stream.Stream;

public abstract class BaseObservableIndividual extends DeepObservable implements Individual {

    public static final Callback<BaseObservableIndividual, Observable[]> EXTRACTOR = obs -> new Observable[]{
            obs.id,
            obs.age,
            obs.vitalStatus,
            obs.sex,
            obs.phenotypicFeatures,
            obs.diseaseStates,
            obs.genotypes
    };

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>(this, "age");
    private final ObjectProperty<ObservableVitalStatus> vitalStatus = new SimpleObjectProperty<>(this, "vitalStatus");
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex", Sex.UNKNOWN);
    private final ListProperty<ObservablePhenotypicFeature> phenotypicFeatures = new SimpleListProperty<>(this, "phenotypicFeatures", FXCollections.observableArrayList(ObservablePhenotypicFeature.EXTRACTOR));
    private final ListProperty<ObservableDiseaseStatus> diseaseStates = new SimpleListProperty<>(this, "diseaseStates", FXCollections.observableArrayList(ObservableDiseaseStatus.EXTRACTOR));
    private final ListProperty<ObservableVariantGenotype> genotypes = new SimpleListProperty<>(this, "genotypes", FXCollections.observableArrayList(ObservableVariantGenotype.EXTRACTOR));

    protected BaseObservableIndividual() {
    }

    protected BaseObservableIndividual(Individual individual) {
        if (individual != null) {
            id.set(individual.getId());

            if (individual.getAge() != null)
                age.set(new ObservableTimeElement(individual.getAge()));

            if (individual.getVitalStatus() != null)
                vitalStatus.set(new ObservableVitalStatus(individual.getVitalStatus()));

            if (individual.getSex() != null)
                sex.set(individual.getSex());

            for (PhenotypicFeature pf : individual.getPhenotypicFeatures())
                phenotypicFeatures.add(new ObservablePhenotypicFeature(pf));

            for (DiseaseStatus ds : individual.getDiseaseStates())
                diseaseStates.add(new ObservableDiseaseStatus(ds));

            for (VariantGenotype vg : individual.getGenotypes())
                genotypes.add(new ObservableVariantGenotype(vg));

        }
    }

    @Override
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    @Override
    public ObservableList<ObservablePhenotypicFeature> getPhenotypicFeatures() {
        return phenotypicFeatures;
    }

    public void setPhenotypicFeatures(ObservableList<ObservablePhenotypicFeature> phenotypicFeatures) {
        this.phenotypicFeatures.set(phenotypicFeatures);
    }

    public ListProperty<ObservablePhenotypicFeature> phenotypicFeaturesProperty() {
        return phenotypicFeatures;
    }

    @Override
    public ObservableList<ObservableDiseaseStatus> getDiseaseStates() {
        return diseaseStates;
    }

    public void setDiseaseStates(ObservableList<ObservableDiseaseStatus> diseaseStates) {
        this.diseaseStates.set(diseaseStates);
    }

    public ListProperty<ObservableDiseaseStatus> diseaseStatesProperty() {
        return diseaseStates;
    }

    @Override
    public ObservableList<ObservableVariantGenotype> getGenotypes() {
        return genotypes;
    }

    public void setGenotypes(ObservableList<ObservableVariantGenotype> genotypes) {
        this.genotypes.set(genotypes);
    }

    public ListProperty<ObservableVariantGenotype> genotypesProperty() {
        return genotypes;
    }

    @Override
    public ObservableTimeElement getAge() {
        return age.get();
    }

    public void setAge(ObservableTimeElement age) {
        this.age.set(age);
    }

    public ObjectProperty<ObservableTimeElement> ageProperty() {
        return age;
    }

    @Override
    public ObservableVitalStatus getVitalStatus() {
        return vitalStatus.get();
    }

    public void setVitalStatus(ObservableVitalStatus vitalStatus) {
        this.vitalStatus.set(vitalStatus);
    }

    public ObjectProperty<ObservableVitalStatus> vitalStatusProperty() {
        return vitalStatus;
    }

    @Override
    public Sex getSex() {
        return sex.get();
    }

    public void setSex(Sex sex) {
        this.sex.set(sex);
    }

    public ObjectProperty<Sex> sexProperty() {
        return sex;
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(age, vitalStatus, phenotypicFeatures, diseaseStates, genotypes);
    }

    @Override
    public String toString() {
        return "BaseObservableIndividual{" +
                "id=" + id.get() +
                ", age=" + age.get() +
                ", vitalStatus=" + vitalStatus.get() +
                ", sex=" + sex.get() +
                ", phenotypicFeatures=" + phenotypicFeatures.stream().map(PhenotypicFeature::toString).toList() +
                ", diseaseStates=" + diseaseStates.get().stream().map(DiseaseStatus::toString).toList() +
                ", genotypes=" + genotypes.get() +
                '}';
    }

}
