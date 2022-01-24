package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.util.*;

public class BaseObservableIndividual {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservableAge> age = new SimpleObjectProperty<>(this, "age", new ObservableAge());
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex", Sex.UNKNOWN);
    private final ObservableList<ObservablePhenotypicFeature> phenotypicFeatures = FXCollections.observableList(new LinkedList<>());
    private final ObservableList<ObservableDiseaseStatus> diseaseStates = FXCollections.observableList(new LinkedList<>());
    private final ObservableMap<String, Genotype> genotypes = FXCollections.observableHashMap();

    public BaseObservableIndividual() {
    }

    protected BaseObservableIndividual(Builder<?> builder) {
        this.id.set(builder.id);
        this.age.get().setYears(builder.age.getYears());
        this.age.get().setMonths(builder.age.getMonths());
        this.age.get().setDays(builder.age.getDays());
        this.sex.set(builder.sex);
        this.phenotypicFeatures.addAll(builder.phenotypicFeatures);
        this.diseaseStates.addAll(builder.diseaseStates);
        this.genotypes.putAll(builder.genotypes);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public ObservableAge getAge() {
        return age.get();
    }

    public void setAge(ObservableAge age) {
        this.age.set(age);
    }

    public ObjectProperty<ObservableAge> ageProperty() {
        return age;
    }

    public Sex getSex() {
        return sex.get();
    }

    public void setSex(Sex sex) {
        this.sex.set(sex);
    }

    public ObjectProperty<Sex> sexProperty() {
        return sex;
    }

    public ObservableList<ObservablePhenotypicFeature> phenotypicFeatures() {
        return phenotypicFeatures;
    }

    public ObservableList<ObservableDiseaseStatus> diseaseStatuses() {
        return diseaseStates;
    }

    public ObservableMap<String, Genotype> genotypes() {
        return genotypes;
    }

    @Override
    public String toString() {
        return "BaseObservableIndividual{" +
                "id=" + id.get() +
                ", age=" + age.get() +
                ", sex=" + sex.get() +
                ", phenotypicFeatures=" + phenotypicFeatures +
                ", diseaseStates=" + diseaseStates +
                ", genotypes=" + genotypes +
                '}';
    }

    public abstract static class Builder<T extends Builder<T>> {

        private final List<ObservablePhenotypicFeature> phenotypicFeatures = new LinkedList<>();
        private final List<ObservableDiseaseStatus> diseaseStates = new LinkedList<>();
        private final Map<String, Genotype> genotypes = new HashMap<>();
        private String id;
        private final ObservableAge age = new ObservableAge();
        private Sex sex;

        protected Builder() {
        }

        public T setId(String id) {
            this.id = id;
            return self();
        }

        protected abstract T self();

        public T setYears(Integer years) {
            this.age.setYears(years);
            return self();
        }

        public T setMonths(int months) {
            this.age.setMonths(months);
            return self();
        }

        public T setDays(int days) {
            this.age.setDays(days);
            return self();
        }

        public T setSex(Sex sex) {
            this.sex = sex;
            return self();
        }

        public T addPhenotypicFeature(ObservablePhenotypicFeature phenotypicFeature) {
            this.phenotypicFeatures.add(phenotypicFeature);
            return self();
        }

        public T addAllPhenotypicFeatures(Collection<ObservablePhenotypicFeature> phenotypicFeatures) {
            this.phenotypicFeatures.addAll(phenotypicFeatures);
            return self();
        }

        public T addDiseaseStatus(ObservableDiseaseStatus diseaseStatus) {
            this.diseaseStates.add(diseaseStatus);
            return self();
        }

        public T addAllDiseaseStatuses(Collection<ObservableDiseaseStatus> diseaseStatuses) {
            this.diseaseStates.addAll(diseaseStatuses);
            return self();
        }

        public T putGenotype(String variantId, Genotype genotype) {
            this.genotypes.put(variantId, genotype);
            return self();
        }

        public T putAllGenotypes(Map<? extends String, ? extends Genotype> genotypes) {
            this.genotypes.putAll(genotypes);
            return self();
        }

        public abstract <U extends BaseObservableIndividual> U build();
    }

}
