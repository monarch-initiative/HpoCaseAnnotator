package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.*;

public class BaseObservableIndividual {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseObservableIndividual.class);

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final StringProperty years = new SimpleStringProperty(this, "years");
    private final ObjectProperty<Integer> months = new SimpleObjectProperty<>(this, "months", 0);
    private final ObjectProperty<Integer> days = new SimpleObjectProperty<>(this, "days", 0);
    private final ObjectProperty<Sex> sex = new SimpleObjectProperty<>(this, "sex", Sex.UNKNOWN);
    private final ObservableList<ObservablePhenotypicFeature> phenotypicFeatures = FXCollections.observableList(new LinkedList<>());
    private final ObservableList<ObservableDiseaseStatus> diseaseStates = FXCollections.observableList(new LinkedList<>());
    private final ObservableMap<String, Genotype> genotypes = FXCollections.observableHashMap();
    private final ObjectBinding<Period> age = ageBinding();

    public BaseObservableIndividual() {
    }

    protected BaseObservableIndividual(Builder<?> builder) {
        this.id.set(builder.id);
        this.years.set(builder.years);
        this.months.set(builder.months);
        this.days.set(builder.days);
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

    public String getYears() {
        return years.get();
    }

    public void setYears(String years) {
        this.years.set(years);
    }

    public StringProperty yearsProperty() {
        return years;
    }

    public int getMonths() {
        return months.get();
    }

    public void setMonths(int months) {
        this.months.set(months);
    }

    public ObjectProperty<Integer> monthsProperty() {
        return months;
    }

    public int getDays() {
        return days.get();
    }

    public void setDays(int days) {
        this.days.set(days);
    }

    public ObjectProperty<Integer> daysProperty() {
        return days;
    }

    public Period getAge() {
        return age.get();
    }

    public ObjectBinding<Period> ageProperty() {
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

    public ObservableList<ObservableDiseaseStatus> diseaseStates() {
        return diseaseStates;
    }

    public ObservableMap<String, Genotype> genotypes() {
        return genotypes;
    }

    private ObjectBinding<Period> ageBinding() {
        return Bindings.createObjectBinding(() -> {
                    String y = years.get();
                    if (y == null) return null;

                    int yr;
                    try {
                        yr = Integer.parseInt(y);
                    } catch (NumberFormatException e) {
                        LOGGER.debug("Error converting years ({}) to an integer: {}", y, e.getMessage());
                        return null;
                    }
                    Integer m = months.get();
                    Integer d = days.get();
                    if (m == null || d == null) {
                        return null;
                    }
                    return Period.of(yr, m, d);
                },
                years, months, days);
    }


    public abstract static class Builder<T extends Builder<T>> {

        private final List<ObservablePhenotypicFeature> phenotypicFeatures = new LinkedList<>();
        private final List<ObservableDiseaseStatus> diseaseStates = new LinkedList<>();
        private final Map<String, Genotype> genotypes = new HashMap<>();
        private String id;
        private String years;
        private int months;
        private int days;
        private Sex sex;

        protected Builder() {
        }

        public T setId(String id) {
            this.id = id;
            return self();
        }

        protected abstract T self();

        public T setYears(String years) {
            this.years = years;
            return self();
        }

        public T setMonths(int months) {
            this.months = months;
            return self();
        }

        public T setDays(int days) {
            this.days = days;
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
