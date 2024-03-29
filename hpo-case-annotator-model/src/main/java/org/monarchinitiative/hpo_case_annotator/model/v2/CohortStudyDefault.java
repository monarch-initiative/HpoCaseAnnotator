package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

class CohortStudyDefault implements CohortStudy {

    private final String id;
    private final Publication publication;
    private final List<CuratedVariant> variants;
    private final List<Individual> members;
    private final StudyMetadata studyMetadata;

    private CohortStudyDefault(String id,
                               Publication publication,
                               List<CuratedVariant> variants,
                               List<Individual> members,
                               StudyMetadata studyMetadata) {
        this.id = Objects.requireNonNull(id, "Id must not be null");
        this.publication = Objects.requireNonNull(publication, "Publication must not be null");
        this.variants = Objects.requireNonNull(variants, "Variants must not be null");
        this.members = Objects.requireNonNull(members, "Members must not be null");
        if (members.isEmpty())
            throw new IllegalArgumentException("At least one member must be present");
        this.studyMetadata = Objects.requireNonNull(studyMetadata, "Study metadata must not be null");
    }

    static CohortStudyDefault of(String id,
                                 Publication publication,
                                 Collection<CuratedVariant> variants,
                                 Collection<? extends Individual> members,
                                 StudyMetadata studyMetadata) {

        return new CohortStudyDefault(id,
                publication,
                List.copyOf(variants),
                List.copyOf(members),
                studyMetadata);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Publication getPublication() {
        return publication;
    }

    @Override
    public List<? extends CuratedVariant> getVariants() {
        return variants;
    }

    @Override
    public List<? extends Individual> getMembers() {
        return members;
    }

    @Override
    public StudyMetadata getStudyMetadata() {
        return studyMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CohortStudyDefault that = (CohortStudyDefault) o;
        return Objects.equals(id, that.id) && Objects.equals(publication, that.publication) && Objects.equals(variants, that.variants) && Objects.equals(members, that.members) && Objects.equals(studyMetadata, that.studyMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publication, variants, members, studyMetadata);
    }

    @Override
    public String toString() {
        return "CohortStudyDefault{" +
                "id='" + id + '\'' +
                ", publication=" + publication +
                ", variants=" + variants +
                ", members=" + members +
                ", studyMetadata=" + studyMetadata +
                '}';
    }
}
