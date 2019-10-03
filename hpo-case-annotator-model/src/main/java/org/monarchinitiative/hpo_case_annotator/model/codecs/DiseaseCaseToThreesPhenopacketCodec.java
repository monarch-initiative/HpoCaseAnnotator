package org.monarchinitiative.hpo_case_annotator.model.codecs;

import com.google.protobuf.Timestamp;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiseaseCaseToThreesPhenopacketCodec extends AbstractDiseaseCaseToPhenopacketCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToThreesPhenopacketCodec.class);

    DiseaseCaseToThreesPhenopacketCodec() {
        // protected no-op
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> mapVariantWithSplicingData() {
        return v -> Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly(hcaGenomeAssemblyToPhenopacketGenomeAssembly(v.getVariantPosition().getGenomeAssembly()))
                        .setChr(v.getVariantPosition().getContig())
                        .setPos(v.getVariantPosition().getPos())
                        .setRef(v.getVariantPosition().getRefAllele())
                        .setAlt(v.getVariantPosition().getAltAllele())
                        .setInfo(encodeInfoData(v))
                        .build())
                .setZygosity(hcaGenotypeToPhenopacketZygosity(v.getGenotype()))
                .build();
    }

    /**
     *
     */
    private static String encodeInfoData(org.monarchinitiative.hpo_case_annotator.model.proto.Variant v) {
        return String.join(";", "VCLASS=" + v.getVariantClass(),
                "PATHOMECHANISM=" + v.getPathomechanism(),
                "CONSEQUENCE=" + v.getConsequence());
    }

    @Override
    public Phenopacket encode(DiseaseCase data) {
        return Phenopacket.newBuilder()
                .setId(Codecs.getPhenopacketIdFor(data))
                .setSubject(Individual.newBuilder()
                        .setId(data.getFamilyInfo().getFamilyOrProbandId())
                        .setSex(hcaSexToPhenopacketSex(data.getFamilyInfo().getSex()))
                        // .setAgeAtCollection() // cannot do this, we would have to enforce age in proper format in HCA first
                        .setTaxonomy(AbstractDiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                        .build())
                .addAllPhenotypicFeatures(data.getPhenotypeList().stream()
                        .map(hcaPhenotypeToPhenopacketPhenotype(data.getPublication()))
                        .collect(Collectors.toList()))
                .addGenes(Gene.newBuilder()
                        .setId(String.format("ENTREZ:%d", data.getGene().getEntrezId()))
                        .setSymbol(data.getGene().getSymbol())
                        .build())
                .addAllVariants(data.getVariantList().stream().map(mapVariantWithSplicingData()).collect(Collectors.toList()))
                .addDiseases(Disease.newBuilder()
                        .setTerm(ontologyClass(String.format("%s:%s", data.getDisease().getDatabase(), data.getDisease().getDiseaseId()), data.getDisease().getDiseaseName()))
                        .build())
                .setMetaData(MetaData.newBuilder()
                        .setCreated(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .build())
                        .setCreatedBy(data.getBiocurator().getBiocuratorId())
                        .setSubmittedBy(data.getSoftwareVersion())
                        .addAllResources(AbstractDiseaseCaseToPhenopacketCodec.makeResources())
                        .setPhenopacketSchemaVersion(phenopacketVersion)
                        .addExternalReferences(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", data.getPublication().getPmid()))
                                .setDescription(data.getPublication().getTitle())
                                .build())
                        .build())
                .build();
    }

    @Override
    public DiseaseCase decode(Phenopacket data) {
        LOGGER.warn("Decoding 3S Phenopacket to DiseaseCase is not supported at the moment");
        return null;
    }

}
