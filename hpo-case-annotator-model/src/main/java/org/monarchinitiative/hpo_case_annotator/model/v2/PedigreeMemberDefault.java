package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.*;

class PedigreeMemberDefault extends IndividualDefault implements PedigreeMember {

    private final String paternalId;
    private final String maternalId;
    private final boolean isProband;

    private PedigreeMemberDefault(String id,
                                  String paternalId,
                                  String maternalId,
                                  Period age,
                                  List<DiseaseStatus> diseases,
                                  Map<String, Genotype> genotypes,
                                  Set<PhenotypicFeature> phenotypicFeatures,
                                  boolean isProband,
                                  Sex sex) {
        super(id, age, phenotypicFeatures, diseases, genotypes, sex);
        this.paternalId = paternalId;
        this.maternalId = maternalId;
        this.isProband = isProband;
    }

    static PedigreeMemberDefault of(String id,
                                    String paternalId,
                                    String maternalId,
                                    Period age,
                                    List<DiseaseStatus> diseases,
                                    Map<String, Genotype> genotypes,
                                    Collection<PhenotypicFeature> phenotypicFeatures,
                                    boolean isProband,
                                    Sex sex) {
        // wrap the collections
        return new PedigreeMemberDefault(Objects.requireNonNull(id, "Individual ID must not be null"),
                paternalId,
                maternalId,
                age,
                List.copyOf(Objects.requireNonNull(diseases, "Diseases must not be null")),
                Map.copyOf(Objects.requireNonNull(genotypes, "Genotypes must not be null")),
                Set.copyOf(Objects.requireNonNull(phenotypicFeatures, "Phenotypic observations must not be null")),
                isProband,
                Objects.requireNonNull(sex, "Sex must not be null"));
    }

    @Override
    public Optional<String> paternalId() {
        return Optional.ofNullable(paternalId);
    }

    @Override
    public Optional<String> maternalId() {
        return Optional.ofNullable(maternalId);
    }

    @Override
    public boolean isProband() {
        return isProband;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PedigreeMemberDefault that = (PedigreeMemberDefault) o;
        return isProband == that.isProband && Objects.equals(paternalId, that.paternalId) && Objects.equals(maternalId, that.maternalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), paternalId, maternalId, isProband);
    }

    @Override
    public String toString() {
        return "PedigreeMemberDefault{" +
                "paternalId='" + paternalId + '\'' +
                ", maternalId='" + maternalId + '\'' +
                ", isProband=" + isProband +
                "} " + super.toString();
    }
}
