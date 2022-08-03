package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.util.*;

class IndividualDefault implements Individual {

    private final String id;
    private final List<PhenotypicFeature> phenotypicFeatures;
    private final List<DiseaseStatus> diseases;
    private final Map<String, Genotype> genotypes;
    private final TimeElement age;
    private final Sex sex;

    protected IndividualDefault(String id,
                                List<PhenotypicFeature> phenotypicFeatures,
                                List<DiseaseStatus> diseases,
                                Map<String, Genotype> genotypes,
                                TimeElement age,
                                Sex sex) {
        // assume that null checks has been made
        this.id = id;
        this.phenotypicFeatures = phenotypicFeatures;
        this.diseases = diseases;
        this.genotypes = genotypes;
        this.age = age;
        this.sex = sex;
    }

    static IndividualDefault of(String id,
                                List<PhenotypicFeature> phenotypicFeatures,
                                List<DiseaseStatus> diseases,
                                Map<String, Genotype> genotypes,
                                TimeElement age,
                                Sex sex) {
        return new IndividualDefault(
                Objects.requireNonNull(id, "Individual ID must not be null"),
                Objects.requireNonNull(phenotypicFeatures, "Phenotypes must not be null"),
                Objects.requireNonNull(diseases, "Diseases must not be null"),
                Objects.requireNonNull(genotypes, "Genotypes must not be null"),
                age,
                Objects.requireNonNull(sex, "Sex must not be null")
        );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TimeElement getAge() {
        return age;
    }

    @Override
    public List<? extends PhenotypicFeature> getPhenotypicFeatures() {
        return phenotypicFeatures;
    }

    @Override
    public List<? extends DiseaseStatus> getDiseaseStates() {
        return diseases;
    }

    @Override
    public Map<String, Genotype> getGenotypes() {
        return genotypes;
    }

    @Override
    public Sex getSex() {
        return sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualDefault that = (IndividualDefault) o;
        return Objects.equals(id, that.id) && Objects.equals(phenotypicFeatures, that.phenotypicFeatures) && Objects.equals(diseases, that.diseases) && Objects.equals(genotypes, that.genotypes) && Objects.equals(age, that.age) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phenotypicFeatures, diseases, genotypes, age, sex);
    }

    @Override
    public String toString() {
        return "IndividualDefault{" +
                "id='" + id + '\'' +
                ", phenotypicFeatures=" + phenotypicFeatures +
                ", diseases=" + diseases +
                ", genotypes=" + genotypes +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
