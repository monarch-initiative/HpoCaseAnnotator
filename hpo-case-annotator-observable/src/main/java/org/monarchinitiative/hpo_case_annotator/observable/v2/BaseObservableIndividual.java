package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.*;

public abstract class BaseObservableIndividual<T extends Individual> implements Individual {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>(this, "age");
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex");
    private final ListProperty<ObservablePhenotypicFeature> phenotypicFeatures = new SimpleListProperty<>(this, "phenotypicFeatures", FXCollections.observableArrayList());
    private final ListProperty<ObservableDiseaseStatus> diseaseStates = new SimpleListProperty<>(this, "diseaseStates", FXCollections.observableArrayList());
    private final ListProperty<ObservableVariantGenotype> genotypes = new SimpleListProperty<>(this, "genotypes", FXCollections.observableArrayList(ObservableVariantGenotype.EXTRACTOR));

    protected BaseObservableIndividual() {
    }

    protected BaseObservableIndividual(T individual) {
        if (individual != null) {
            id.set(individual.getId());
            age.set(new ObservableTimeElement(individual.getAge()));
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
    public List<? extends PhenotypicFeature> getPhenotypicFeatures() {
        return phenotypicFeatures;
    }

    public ListProperty<ObservablePhenotypicFeature> getObservablePhenotypicFeatures() {
        return phenotypicFeatures;
    }


    @Override
    public ObservableList<? extends DiseaseStatus> getDiseaseStates() {
        return diseaseStates;
    }

    public ListProperty<ObservableDiseaseStatus> diseaseStatesProperty() {
        return diseaseStates;
    }

    public void setDiseaseStates(ObservableList<ObservableDiseaseStatus> diseaseStates) {
        this.diseaseStates.set(diseaseStates);
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
    public String toString() {
        return "BaseObservableIndividual{" +
                "id=" + id.get() +
                ", age=" + age.get() +
                ", sex=" + sex.get() +
                ", phenotypicFeatures=" + phenotypicFeatures.stream().map(PhenotypicFeature::toString).toList() +
                ", diseaseStates=" + diseaseStates.get().stream().map(DiseaseStatus::toString).toList() +
                ", genotypes=" + genotypes.get() +
                '}';
    }

}
