package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.util.*;

public abstract class BaseObservableIndividual<T extends Individual> implements Individual {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>(this, "age");
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex");
    private final ListProperty<ObservablePhenotypicFeature> phenotypicFeatures = new SimpleListProperty<>(this, "phenotypicFeatures", FXCollections.observableArrayList());
    private final ListProperty<ObservableDiseaseStatus> diseaseStates = new SimpleListProperty<>(this, "diseaseStates", FXCollections.observableArrayList());
    private final MapProperty<String, Genotype> genotypes = new SimpleMapProperty<>(this, "genotypes", FXCollections.observableHashMap());

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

            genotypes.putAll(individual.getGenotypes());
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
    public MapProperty<String, Genotype> getGenotypes() {
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
