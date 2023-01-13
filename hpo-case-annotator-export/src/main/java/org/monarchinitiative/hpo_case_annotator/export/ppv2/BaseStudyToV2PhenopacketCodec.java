package org.monarchinitiative.hpo_case_annotator.export.ppv2;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.monarchinitiative.hpo_case_annotator.model.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.phenopackettools.builder.builders.DiseaseBuilder;
import org.phenopackets.phenopackettools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.phenopackettools.builder.builders.Resources;
import org.phenopackets.phenopackettools.builder.builders.TimeElements;
import org.phenopackets.schema.v2.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.phenopackets.phenopackettools.builder.builders.OntologyClassBuilder.ontologyClass;

/**
 * A base class with export routines shared between all top-level elements of Phenopacket Schema.
 */
abstract class BaseStudyToV2PhenopacketCodec<STUDY extends Study, MSG extends Message> implements Codec<STUDY, MSG> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStudyToV2PhenopacketCodec.class);
    private static final String PHENOPACKET_SCHEMA_VERSION = "2.0.2";

    final Ontology hpo;
    private final List<Resource> resources;

    BaseStudyToV2PhenopacketCodec(Ontology hpo) {
        this.hpo = Objects.requireNonNull(hpo);
        this.resources = List.of(
                Resources.hpoVersion("2018-03-08"),
                Resources.patoVersion("2018-03-28"),
                Resources.genoVersion("19-03-2018"),
                Resources.ncbiTaxonVersion("2018-03-02"),
                Resources.ecoVersion("2018-11-10"),
                Resources.omimVersion(hpo.version().orElse("UNKNOWN"))
        );
    }

    Stream<org.phenopackets.schema.v2.core.PhenotypicFeature> mapToPhenotypicFeature(PhenotypicFeature pf) {
        // Update the term ID
        String id, label;
        TermId primaryTermId = hpo.getPrimaryTermId(pf.getId());
        if (!primaryTermId.equals(pf.getId())) {
            // Non-primary term in the phenotypic feature, we must update!
            id = primaryTermId.getValue();
            Term term = hpo.getTermMap().get(primaryTermId);
            if (term == null) {
                LOGGER.warn("");
                return Stream.empty();
            }
            label = term.getName();
        } else {
            id = pf.getId().getValue();
            label = pf.getLabel();
        }

        PhenotypicFeatureBuilder pfb = PhenotypicFeatureBuilder.builder(ontologyClass(id, label));

        // observation status
        if (pf.isExcluded())
            pfb.excluded();

        // onset
        if (pf.getOnset() != null)
            mapTimeElement(pf.getOnset())
                    .ifPresentOrElse(
                            pfb::onset,
                            () -> LOGGER.warn("Unable to map onset of {}", pf.getId().getValue())
                    );

        // resolution
        if (pf.getResolution() != null)
            mapTimeElement(pf.getResolution())
                    .ifPresentOrElse(
                            pfb::resolution,
                            () -> LOGGER.warn("Unable to map resolution of {}", pf.getId().getValue())
                    );

        return Stream.of(pfb.build());
    }

    static Optional<org.phenopackets.schema.v2.core.TimeElement> mapTimeElement(org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement age) {
        if (age == null)
            return Optional.empty();
        
        return switch (age.getTimeElementCase()) {
            case GESTATIONAL_AGE -> {
                GestationalAge ga = age.getGestationalAge();
                if (ga.getWeeks() == null && ga.getDays() == null) {
                    yield Optional.empty();
                }
                if (ga.getDays() == null)
                    yield Optional.of(TimeElements.gestationalAge(ga.getWeeks()));
                else if (ga.getWeeks() == null)
                    yield Optional.of(TimeElements.gestationalAge(0, ga.getDays()));
                else
                    yield Optional.of(TimeElements.gestationalAge(ga.getWeeks(), ga.getDays()));
            }
            case AGE -> {
                Age a = age.getAge();
                yield a.getIso8601Duration()
                        .map(TimeElements::age);
            }
            case AGE_RANGE -> {
                AgeRange ar = age.getAgeRange();
                Optional<String> start = ar.getStart().getIso8601Duration();
                Optional<String> end = ar.getEnd().getIso8601Duration();

                if (start.isPresent() && end.isPresent()) {
                    yield Optional.of(TimeElements.ageRange(start.get(), end.get()));
                } else {
                    LOGGER.warn("Unable to convert incomplete age range");
                    yield Optional.empty();
                }
            }
            case ONTOLOGY_CLASS -> {
                OntologyClass oc = age.getOntologyClass();
                String label = oc.getLabel();
                yield Optional.of(TimeElements.ontologyClass(ontologyClass(oc.getId().getValue(), label)));
            }
        };
    }

    MetaData encodeMetaData(Publication publication, StudyMetadata studyMetadata) {
        MetaData.Builder builder = MetaData.newBuilder()
                .setCreated(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setCreatedBy(studyMetadata.getCreatedBy().getSoftwareVersion())
                .setSubmittedBy(curatorId(studyMetadata))
                .setPhenopacketSchemaVersion(PHENOPACKET_SCHEMA_VERSION)
                .addAllResources(resources);

        if (publication != null)
            builder.addExternalReferences(ExternalReference.newBuilder()
                    .setId(String.format("PMID:%s", publication.getPmid()))
                    .setDescription(publication.getTitle())
                    .build());

        return builder.build();
    }

    private static String curatorId(StudyMetadata studyMetadata) {
        return studyMetadata.getModifiedBy().stream()
                .max(Comparator.comparing(EditHistory::getTimestamp))
                .map(EditHistory::getCuratorId)
                .orElse("UNKNOWN");
    }

    static Stream<Disease> mapToDisease(DiseaseStatus ds) {
        DiseaseIdentifier diseaseId = ds.getDiseaseId();
        if (diseaseId == null)
            return Stream.empty();

        DiseaseBuilder builder = DiseaseBuilder.builder(diseaseId.id().getValue(), diseaseId.getDiseaseName());
        if (ds.isExcluded())
            builder.excluded();

        return Stream.of(builder.build());
    }
}
