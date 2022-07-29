package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.*;

public abstract class BaseObservableIndividual<T extends Individual> implements Individual, Updateable<T> {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservableAge> observableAge = new SimpleObjectProperty<>(this, "observableAge");
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex");
    private final ListProperty<ObservablePhenotypicFeature> phenotypicFeatures = new SimpleListProperty<>(this, "phenotypicFeatures", FXCollections.observableArrayList());
    private final ListProperty<ObservableDiseaseStatus> diseaseStates = new SimpleListProperty<>(this, "diseaseStates", FXCollections.observableArrayList());
    private final MapProperty<String, Genotype> genotypes = new SimpleMapProperty<>(this, "genotypes", FXCollections.observableHashMap());

    protected BaseObservableIndividual() {
    }

    protected BaseObservableIndividual(Builder<?> builder) {
        this.id.set(builder.id);
        this.observableAge.get().setYears(builder.age.getYears());
        this.observableAge.get().setMonths(builder.age.getMonths());
        this.observableAge.get().setDays(builder.age.getDays());
        this.sex.set(builder.sex);
        this.phenotypicFeatures.addAll(builder.phenotypicFeatures);
        this.diseaseStates.addAll(builder.diseaseStates);
        this.genotypes.putAll(builder.genotypes);
    }

    protected BaseObservableIndividual(T individual) {
        if (individual != null) {
            id.set(individual.getId());
            observableAge.set(individual.getAge().map(ObservableAge::new).orElse(null));
            sex.set(individual.getSex());

            for (PhenotypicFeature pf : individual.getPhenotypicFeatures())
                phenotypicFeatures.add(new ObservablePhenotypicFeature(pf));

            for (DiseaseStatus ds : individual.getDiseases())
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
    public List<? extends DiseaseStatus> getDiseases() {
        return diseaseStates;
    }

    public ListProperty<ObservableDiseaseStatus> getObservableDiseases() {
        return diseaseStates;
    }

    @Override
    public MapProperty<String, Genotype> getGenotypes() {
        return genotypes;
    }

    @Override
    public Optional<Age> getAge() {
        return Optional.ofNullable(observableAge.get());
    }

    public ObservableAge getObservableAge() {
        return observableAge.get();
    }

    public void setObservableAge(ObservableAge age) {
        this.observableAge.set(age);
    }

    public ObjectProperty<ObservableAge> observableAgeProperty() {
        return observableAge;
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
    public <U extends T> void update(U data) {
        if (data == null) {
            setId(null);
            getObservableAge().update(null);
            setSex(null);
            getPhenotypicFeatures().clear();
            getDiseases().clear();
            getGenotypes().clear();
        } else {
            setId(data.getId());
//            if (observableAge.get() == null)
//            getObservableAge().update(data.getAge().orElse(null));
            setSex(data.getSex());
            Updateable.updateObservableList(data.getPhenotypicFeatures(), getObservablePhenotypicFeatures(), ObservablePhenotypicFeature::new);
            Updateable.updateObservableList(data.getDiseases(), getObservableDiseases(), ObservableDiseaseStatus::new);

            // We should be OK with the simple replacement as Genotype is not observable.
            getGenotypes().clear();
            getGenotypes().putAll(data.getGenotypes());
        }
    }

    @Override
    public String toString() {
        return "BaseObservableIndividual{" +
                "id=" + id.get() +
                ", age=" + observableAge.get() +
                ", sex=" + sex.get() +
                ", phenotypicFeatures=" + phenotypicFeatures.get() +
                ", diseaseStates=" + diseaseStates.get() +
                ", genotypes=" + genotypes.get() +
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
