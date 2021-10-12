package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

class CohortStudyDefault implements CohortStudy {

    private final Publication publication;
    private final List<CuratedVariant> variants;
    private final Set<Individual> members;
    private final StudyMetadata studyMetadata;

    private CohortStudyDefault(Publication publication,
                               List<CuratedVariant> variants,
                               Set<Individual> members,
                               StudyMetadata studyMetadata) {
        this.publication = publication;
        this.variants = variants;
        this.members = members;
        this.studyMetadata = studyMetadata;
    }

    static CohortStudyDefault of(Publication publication,
                                 Collection<CuratedVariant> variants,
                                 Collection<? extends Individual> members,
                                 StudyMetadata studyMetadata) {
        Objects.requireNonNull(publication, "Publication must not be null");
        Objects.requireNonNull(variants, "Variants must not be null");
        Objects.requireNonNull(members, "Members must not be null");
        if (members.isEmpty())
            throw new IllegalArgumentException("At least one member must be present");

        Objects.requireNonNull(studyMetadata, "Study metadata must not be null");

        return new CohortStudyDefault(publication,
                List.copyOf(variants),
                Set.copyOf(members),
                studyMetadata);
    }

    @Override
    public Publication publication() {
        return publication;
    }

    @Override
    public List<CuratedVariant> variants() {
        return variants;
    }

    @Override
    public Stream<? extends Individual> members() {
        return members.stream();
    }

    @Override
    public StudyMetadata studyMetadata() {
        return studyMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CohortStudyDefault that = (CohortStudyDefault) o;
        return Objects.equals(publication, that.publication) && Objects.equals(variants, that.variants) && Objects.equals(members, that.members) && Objects.equals(studyMetadata, that.studyMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publication, variants, members, studyMetadata);
    }

    @Override
    public String toString() {
        return "CohortStudyDefault{" +
                "publication=" + publication +
                ", variants=" + variants +
                ", individuals=" + members +
                ", studyMetadata=" + studyMetadata +
                '}';
    }
}
