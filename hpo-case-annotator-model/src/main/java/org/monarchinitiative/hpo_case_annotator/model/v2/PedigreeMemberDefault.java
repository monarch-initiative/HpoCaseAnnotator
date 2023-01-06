package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.*;

class PedigreeMemberDefault extends IndividualDefault implements PedigreeMember {

    private final String paternalId;
    private final String maternalId;
    private final boolean isProband;

    private PedigreeMemberDefault(String id,
                                  String paternalId,
                                  String maternalId,
                                  boolean isProband,
                                  List<PhenotypicFeature> phenotypicFeatures,
                                  List<DiseaseStatus> diseases,
                                  List<VariantGenotype> genotypes,
                                  TimeElement age,
                                  VitalStatus vitalStatus,
                                  Sex sex) {
        super(id, phenotypicFeatures, diseases, genotypes, age, vitalStatus, sex);
        this.paternalId = paternalId;
        this.maternalId = maternalId;
        this.isProband = isProband;
    }

    static PedigreeMemberDefault of(String id,
                                    String paternalId,
                                    String maternalId,
                                    boolean isProband,
                                    Collection<? extends PhenotypicFeature> phenotypicFeatures,
                                    List<? extends DiseaseStatus> diseases,
                                    List<? extends VariantGenotype> genotypes,
                                    TimeElement age,
                                    VitalStatus vitalStatus,
                                    Sex sex) {
        // wrap the collections
        return new PedigreeMemberDefault(Objects.requireNonNull(id, "Individual ID must not be null"),
                paternalId,
                maternalId,
                isProband,
                List.copyOf(Objects.requireNonNull(phenotypicFeatures, "Phenotypic observations must not be null")),
                List.copyOf(Objects.requireNonNull(diseases, "Diseases must not be null")),
                List.copyOf(Objects.requireNonNull(genotypes, "Genotypes must not be null")),
                age,
                vitalStatus,
                Objects.requireNonNull(sex, "Sex must not be null"));
    }

    @Override
    public Optional<String> getPaternalId() {
        return Optional.ofNullable(paternalId);
    }

    @Override
    public Optional<String> getMaternalId() {
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
