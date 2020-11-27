package org.monarchinitiative.hpo_case_annotator.model.codecs;

import com.google.protobuf.Timestamp;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * This codec converts {@link DiseaseCase} into {@link Phenopacket}. Phenopacket is meant to be used as an interchange
 * format between various programs.
 * <p>
 * Both conversions are lossy since neither format is a complete superset of the other.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class DiseaseCaseToPhenopacketCodec extends AbstractDiseaseCaseToPhenopacketCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToPhenopacketCodec.class);

    DiseaseCaseToPhenopacketCodec() {
        // package-private no-op
    }

    /**
     * Encode {@link DiseaseCase} into {@link Phenopacket}.
     *
     * @param data {@link DiseaseCase} instance to be encoded
     * @return {@link Phenopacket}
     */
    @Override
    public Phenopacket encode(DiseaseCase data) {
        String familyOrProbandId = data.getFamilyInfo().getFamilyOrProbandId();
        Publication publication = data.getPublication();
        return Phenopacket.newBuilder()
                // id
                .setId(Codecs.getPhenopacketIdFor(data))
                // subject/individual and the publication data
                .setSubject(Individual.newBuilder()
                        .setId(familyOrProbandId)
                        .setAgeAtCollection(Age.newBuilder().setAge(data.getFamilyInfo().getAge()).build())
                        .setSex(hcaSexToPhenopacketSex(data.getFamilyInfo().getSex()))
                        .setTaxonomy(HOMO_SAPIENS)
                        .build())
                // phenotype (HPO) terms
                .addAllPhenotypicFeatures(data.getPhenotypeList().stream()
                        .map(hcaPhenotypeToPhenopacketPhenotype(publication))
                        .collect(Collectors.toList()))
                // gene in question
                .addGenes(Gene.newBuilder()
                        .setId("NCBIGene:" + data.getGene().getEntrezId())
                        .setSymbol(data.getGene().getSymbol())
                        .build())
                // variants, genome assembly
                .addAllVariants(data.getVariantList().stream()
                        .map(hcaVariantToPhenopacketVariant())
                        .collect(Collectors.toList()))
                // disease
                .addDiseases(Disease.newBuilder()
                        .setTerm(ontologyClass(data.getDisease().getDatabase() + ":" + data.getDisease().getDiseaseId(), data.getDisease().getDiseaseName()))
                        .build())
                // metadata - Biocurator ID, ontologies used
                .setMetaData(MetaData.newBuilder()
                        .setCreated(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                        .setCreatedBy(data.getSoftwareVersion())
                        .setSubmittedBy(data.getBiocurator().getBiocuratorId())
                        .setPhenopacketSchemaVersion(phenopacketVersion)
                        .addAllResources(RESOURCES)
                        .addExternalReferences(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", data.getPublication().getPmid()))
                                .setDescription(data.getPublication().getTitle())
                                .build())
                        .build())
                .build();
    }


    @Override
    public DiseaseCase decode(Phenopacket data) {
        // TODO - implement phenopacket to diseasecase conversion
        LOGGER.warn("Not yet supported");
        return null;
    }

}
