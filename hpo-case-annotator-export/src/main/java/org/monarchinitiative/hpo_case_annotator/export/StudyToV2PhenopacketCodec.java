package org.monarchinitiative.hpo_case_annotator.export;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.monarchinitiative.hpo_case_annotator.model.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.phenopackets.phenopackettools.builder.FamilyBuilder;
import org.phenopackets.phenopackettools.builder.PhenopacketBuilder;
import org.phenopackets.phenopackettools.builder.builders.IndividualBuilder;
import org.phenopackets.schema.v2.Cohort;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.ExternalReference;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.Resource;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

class StudyToV2PhenopacketCodec implements Codec<Study, Message> {

    static final List<Resource> RESOURCES = Arrays.asList(
            Resource.newBuilder()
                    .setId("hp")
                    .setName("human phenotype ontology")
                    .setNamespacePrefix("HP")
                    .setIriPrefix("http://purl.obolibrary.org/obo/HP_")
                    .setUrl("http://purl.obolibrary.org/obo/hp.owl")
                    .setVersion("2018-03-08")
                    .build(),
            Resource.newBuilder()
                    .setId("pato")
                    .setName("Phenotype And Trait Ontology")
                    .setNamespacePrefix("PATO")
                    .setIriPrefix("http://purl.obolibrary.org/obo/PATO_")
                    .setUrl("http://purl.obolibrary.org/obo/pato.owl")
                    .setVersion("2018-03-28")
                    .build(),
            Resource.newBuilder()
                    .setId("geno")
                    .setName("Genotype Ontology")
                    .setNamespacePrefix("GENO")
                    .setIriPrefix("http://purl.obolibrary.org/obo/GENO_")
                    .setUrl("http://purl.obolibrary.org/obo/geno.owl")
                    .setVersion("19-03-2018")
                    .build(),
            Resource.newBuilder()
                    .setId("ncbitaxon")
                    .setName("NCBI organismal classification")
                    .setNamespacePrefix("NCBITaxon")
                    .setUrl("http://purl.obolibrary.org/obo/ncbitaxon.owl")
                    .setVersion("2018-03-02")
                    .build(),
            Resource.newBuilder()
                    .setId("eco")
                    .setName("Evidence and Conclusion Ontology")
                    .setNamespacePrefix("ECO")
                    .setIriPrefix("http://purl.obolibrary.org/obo/ECO_")
                    .setUrl("http://purl.obolibrary.org/obo/eco.owl")
                    .setVersion("2018-11-10")
                    .build(),
            Resource.newBuilder()
                    .setId("omim")
                    .setName("Online Mendelian Inheritance in Man")
                    .setNamespacePrefix("OMIM")
                    .setUrl("https://www.omim.org")
                    .build()
    );
    private static final String PHENOPACKET_SCHEMA_VERSION = "2.0.1";
    private static final StudyToV2PhenopacketCodec INSTANCE = new StudyToV2PhenopacketCodec(); // singleton

    static StudyToV2PhenopacketCodec instance() {
        return INSTANCE;
    }

    private StudyToV2PhenopacketCodec() {
    }

    @Override
    public Message encode(Study study) throws ModelTransformationException {
        if (study instanceof FamilyStudy family) {
            return encodeFamilyStudy(family);
        } else if (study instanceof CohortStudy cohort) {
            return encodeCohortStudy(cohort);
        } else
            throw new ModelTransformationException("Unable to encode instance of %s".formatted(study.getClass().getSimpleName()));
    }

    private Message encodeFamilyStudy(FamilyStudy family) throws ModelTransformationException {
        // TODO - implement
//        Stream<? extends PedigreeMember> members = family.members();
//        PhenopacketBuilder pp = PhenopacketBuilder.create(members);
        PedigreeMember proband = findProband(family.pedigree());
        MetaData metaData = hcaMetaData(family.publication(), family.studyMetadata());
        return FamilyBuilder.create(family.id())
                .phenopacket(mapToPhenopacket(proband, metaData))
                .metaData(metaData)
                .build();
    }

    private static Phenopacket mapToPhenopacket(PedigreeMember proband, MetaData metaData) {
        // TODO - implement
        IndividualBuilder individual = IndividualBuilder.builder(proband.id())
                .homoSapiens();
        // sex - TODO - karyotypic sex
        individual = switch (proband.sex()) {
            case MALE -> individual.male();
            case FEMALE -> individual.female();
            case UNKNOWN -> individual.unknownSex();
        };



        // TODO - age at last encounter, vital status
        return PhenopacketBuilder.create(proband.id(), metaData)
                .individual(individual
                        .build())
                .build();
    }

    private static PedigreeMember findProband(Pedigree pedigree) throws ModelTransformationException {
        List<PedigreeMember> probands = pedigree.members()
                .filter(PedigreeMember::isProband)
                .toList();
        if (probands.size() != 1)
            throw new ModelTransformationException("There must be exactly 1 proband in the family but found %d".formatted(probands.size()));

        return probands.get(0);
    }

    private Message encodeCohortStudy(CohortStudy cohort) {
        // Note that Hpo case annotator treats a case report as a cohort with size 1.
        // Therefore, we should consider exporting a `CohortStudy` containing a single individual as `Phenopacket`
        // and not as `Cohort` phenopacket structure.
        // TODO - implement
        return Cohort.newBuilder()
                .setId(cohort.id())
                .setDescription(cohort.studyMetadata().freeText())
                .addAllMembers(cohort.members()
                        .map(cohortMemberToPhenopacket())
                        .toList())
                .setMetaData(hcaMetaData(cohort.publication(), cohort.studyMetadata()))
                .build();
    }

    private static Function<Individual, Phenopacket> cohortMemberToPhenopacket() {
        // TODO - implement
        return i -> null;
    }

    private MetaData hcaMetaData(Publication publication, StudyMetadata studyMetadata) {
        return MetaData.newBuilder()
                .setCreated(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setCreatedBy(studyMetadata.createdBy().softwareVersion())
                .setSubmittedBy(curatorId(studyMetadata))
                .setPhenopacketSchemaVersion(PHENOPACKET_SCHEMA_VERSION)
                .addAllResources(RESOURCES)
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId(String.format("PMID:%s", publication.pmid()))
                        .setDescription(publication.title())
                        .build())
                .build();
    }

    private static String curatorId(StudyMetadata studyMetadata) {
        return studyMetadata.modifiedBy().stream()
                .max(Comparator.comparing(EditHistory::timestamp))
                .map(EditHistory::curatorId)
                .orElse("UNKNOWN");
    }
}
