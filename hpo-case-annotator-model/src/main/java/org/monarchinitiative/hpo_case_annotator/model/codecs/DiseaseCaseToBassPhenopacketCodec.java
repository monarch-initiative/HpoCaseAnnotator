package org.monarchinitiative.hpo_case_annotator.model.codecs;

import com.google.protobuf.Timestamp;
import org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.utils.ModelUtils;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.Gene;
import org.phenopackets.schema.v1.core.Sex;
import org.phenopackets.schema.v1.core.Variant;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiseaseCaseToBassPhenopacketCodec implements Codec<DiseaseCase, Phenopacket> {

    static final String DATASET_ID = "BASS";

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToBassPhenopacketCodec.class);

    DiseaseCaseToBassPhenopacketCodec() {
        // package-private no-op
    }

    private static Sex hcaSexToPhenopacketSex(org.monarchinitiative.hpo_case_annotator.model.proto.Sex sex) {
        switch (sex) {
            case MALE:
                return Sex.MALE;
            case FEMALE:
                return Sex.FEMALE;
            case UNKNOWN_SEX:
            default:
                return Sex.UNKNOWN_SEX;

        }
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> hcaVariantToPhenopacketVariant() {
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

    private static String hcaGenomeAssemblyToPhenopacketGenomeAssembly(GenomeAssembly assembly) {
        switch (assembly) {
            case GRCH_37:
                return "GRCh37";
            case GRCH_38:
                return "GRCh38";
            case UNKNOWN_GENOME_ASSEMBLY:
            case UNRECOGNIZED:
            default:
                return "UNKNOWN";
        }
    }

    /**
     *
     */
    private static String encodeInfoData(org.monarchinitiative.hpo_case_annotator.model.proto.Variant v) {
        return String.join(";", "VCLASS=" + v.getVariantClass(),
                "PATHOMECHANISM=" + v.getPathomechanism(),
                "CONSEQUENCE=" + v.getConsequence());
    }

    private static org.phenopackets.schema.v1.core.OntologyClass hcaGenotypeToPhenopacketZygosity(Genotype genotype) {
        switch (genotype) {
            case HETEROZYGOUS:
                return DiseaseCaseToPhenopacketCodec.HET;
            case HOMOZYGOUS_ALTERNATE:
                return DiseaseCaseToPhenopacketCodec.HOM_ALT;
            case HEMIZYGOUS:
                return DiseaseCaseToPhenopacketCodec.HEMIZYGOUS;
            case HOMOZYGOUS_REFERENCE:
            case UNDEFINED:
            default:
                return org.phenopackets.schema.v1.core.OntologyClass.getDefaultInstance();
        }
    }

    private static Function<OntologyClass, Phenotype> hcaPhenotypeToPhenopacketPhenotype(Publication publication) {
        return oc -> Phenotype.newBuilder()
                .setType(org.phenopackets.schema.v1.core.OntologyClass.newBuilder().setId(oc.getId()).setLabel(oc.getLabel()).build())
                .setNegated(oc.getNotObserved())
                .addEvidence(Evidence.newBuilder()
                        .setEvidenceCode(DiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                        .setReference(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", publication.getPmid()))
                                .setDescription(publication.getTitle())
                                .build())
                        .build())
                .build();
    }

    @Override
    public Phenopacket encode(DiseaseCase data) {
        return Phenopacket.newBuilder()
                .setId(ModelUtils.getFileNameFor(data))
                .setSubject(Individual.newBuilder()
                        .setId(data.getFamilyInfo().getFamilyOrProbandId())
                        .setDatasetId(DATASET_ID)
                        .setSex(hcaSexToPhenopacketSex(data.getFamilyInfo().getSex()))
                        // .setAgeAtCollection() // cannot do this, we would have to enforce age in proper format in HCA first
                        .setTaxonomy(DiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                        .build())
                .addAllPhenotypes(data.getPhenotypeList().stream()
                        .map(hcaPhenotypeToPhenopacketPhenotype(data.getPublication()))
                        .collect(Collectors.toList()))
                .addGenes(Gene.newBuilder()
                        .setId(String.format("ENTREZ:%d", data.getGene().getEntrezId()))
                        .setSymbol(data.getGene().getSymbol())
                        .build())
                .addAllVariants(data.getVariantList().stream().map(hcaVariantToPhenopacketVariant()).collect(Collectors.toList()))
                .addDiseases(Disease.newBuilder()
                        .setTerm(org.phenopackets.schema.v1.core.OntologyClass.newBuilder()
                                .setId(String.format("%s:%s", data.getDisease().getDatabase(), data.getDisease().getDiseaseId()))
                                .setLabel(data.getDisease().getDiseaseName())
                                .build())
                        .build())
                .setMetaData(MetaData.newBuilder()
                        .setCreated(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .build())
                        .setCreatedBy(data.getBiocurator().getBiocuratorId())
                        .setSubmittedBy(data.getSoftwareVersion())
                        .addAllResources(DiseaseCaseToPhenopacketCodec.makeResources())
                        .addExternalReferences(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", data.getPublication().getPmid()))
                                .setDescription(data.getPublication().getTitle())
                                .build())
                        .build())
                .build();
    }

    @Override
    public DiseaseCase decode(Phenopacket data) {
        LOGGER.warn("Decoding BASS Phenopacket to DiseaseCase is not supported at the moment");
        return null;
    }
}
