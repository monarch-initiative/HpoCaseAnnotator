package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.*;
import java.util.stream.Stream;

class IndividualDefault implements Individual {

    private final String id;
    private final Period age;
    private final Set<PhenotypicFeature> phenotypicFeatures;
    private final List<DiseaseStatus> diseases;
    private final Map<String, Genotype> genotypes;
    private final Sex sex;

    protected IndividualDefault(String id,
                                Period age,
                                Set<PhenotypicFeature> phenotypicFeatures,
                                List<DiseaseStatus> diseases,
                                Map<String, Genotype> genotypes,
                                Sex sex) {
        // assume that null checks has been made
        this.id = id;
        this.age = age;
        this.phenotypicFeatures = phenotypicFeatures;
        this.diseases = diseases;
        this.genotypes = genotypes;
        this.sex = sex;
    }

    static IndividualDefault of(String id,
                                Period age,
                                Collection<PhenotypicFeature> phenotypicFeatures,
                                List<DiseaseStatus> diseases,
                                Map<String, Genotype> genotypes,
                                Sex sex) {
        return new IndividualDefault(Objects.requireNonNull(id, "Individual ID must not be null"),
                age,
                Set.copyOf(Objects.requireNonNull(phenotypicFeatures, "Phenotypes must not be null")),
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
    public Stream<PhenotypicFeature> phenotypicFeatures() {
        return phenotypicFeatures.stream();
    }

    @Override
    public List<DiseaseStatus> diseases() {
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
        return Objects.equals(id, that.id) && Objects.equals(age, that.age) && Objects.equals(phenotypicFeatures, that.phenotypicFeatures) && Objects.equals(diseases, that.diseases) && Objects.equals(genotypes, that.genotypes) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, phenotypicFeatures, diseases, genotypes, sex);
    }

    @Override
    public String toString() {
        return "IndividualDefault{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", phenotypicFeatures=" + phenotypicFeatures +
                ", diseases=" + diseases +
                ", genotypes=" + genotypes +
                ", sex=" + sex +
                '}';
    }
}
