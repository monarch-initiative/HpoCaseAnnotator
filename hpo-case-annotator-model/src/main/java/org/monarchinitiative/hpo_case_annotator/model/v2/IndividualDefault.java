package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class IndividualDefault implements Individual {

    private final String id;
    private final Period age;
    private final List<PhenotypicObservation> phenotypicObservations;
    private final List<Disease> diseases;
    private final Map<String, Genotype> genotypes;
    private final Sex sex;

    protected IndividualDefault(String id,
                                Period age,
                                List<PhenotypicObservation> phenotypicObservations,
                                List<Disease> diseases,
                                Map<String, Genotype> genotypes,
                                Sex sex) {
        // assume that null checks has been made
        this.id = id;
        this.age = age;
        this.phenotypicObservations = phenotypicObservations;
        this.diseases = diseases;
        this.genotypes = genotypes;
        this.sex = sex;
    }

    static IndividualDefault of(String id,
                                Period age,
                                List<PhenotypicObservation> phenotypicObservations,
                                List<Disease> diseases,
                                Map<String, Genotype> genotypes,
                                Sex sex) {
        return new IndividualDefault(Objects.requireNonNull(id, "Individual ID must not be null"),
                age,
                Objects.requireNonNull(phenotypicObservations, "Phenotypes must not be null"),
                Objects.requireNonNull(diseases, "Diseases must not be null"),
                Objects.requireNonNull(genotypes, "Genotypes must not be null"),
                Objects.requireNonNull(sex, "Sex must not be null"));
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Optional<Period> age() {
        return Optional.ofNullable(age);
    }

    @Override
    public List<PhenotypicObservation> phenotypicObservations() {
        return phenotypicObservations;
    }

    @Override
    public List<Disease> diseases() {
        return diseases;
    }

    @Override
    public Map<String, Genotype> genotypes() {
        return genotypes;
    }

    @Override
    public Sex sex() {
        return sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualDefault that = (IndividualDefault) o;
        return Objects.equals(id, that.id) && Objects.equals(age, that.age) && Objects.equals(phenotypicObservations, that.phenotypicObservations) && Objects.equals(diseases, that.diseases) && Objects.equals(genotypes, that.genotypes) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, phenotypicObservations, diseases, genotypes, sex);
    }

    @Override
    public String toString() {
        return "IndividualDefault{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", phenotypicObservations=" + phenotypicObservations +
                ", diseases=" + diseases +
                ", genotypes=" + genotypes +
                ", sex=" + sex +
                '}';
    }
}
