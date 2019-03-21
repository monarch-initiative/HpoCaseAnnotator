package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.proto.Sex;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.*;
import org.phenopackets.schema.v1.io.PhenoPacketFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class converts {@link DiseaseCase} to {@link org.phenopackets.schema.v1.PhenoPacket} and back.
 */
public class PhenoPacketCodec {

    // source https://bioportal.bioontology.org/ontologies/ECO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FECO_0000033&jump_to_nav=true
    public static final OntologyClass TRACEABLE_AUTHOR_STATEMENT = ontologyClass("ECO:0000033", "author statement supported by traceable reference");

    public static final OntologyClass HOMO_SAPIENS = ontologyClass("NCBITaxon:9606", "Homo sapiens");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000136
    public static final OntologyClass HOM_ALT = ontologyClass("GENO:0000136", "homozygous");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000135
    public static final OntologyClass HET = ontologyClass("GENO:0000135", "heterozygous");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000134
    public static final OntologyClass HEMIZYGOUS = ontologyClass("GENO:0000134", "hemizygous");

    public static final List<Resource> RESOURCES = makeResources();

    private PhenoPacketCodec() {
        // no-op, static utility class
    }

    /**
     * @param diseaseCase {@link DiseaseCase} to be converted to phenopacket
     * @return {@link PhenoPacket} with data from <code>diseaseCase</code>
     */
    public static PhenoPacket diseaseCaseToPhenopacket(DiseaseCase diseaseCase) {
        String familyOrProbandId = diseaseCase.getFamilyInfo().getFamilyOrProbandId();
        Publication publication = diseaseCase.getPublication();
        String metadata = diseaseCase.getMetadata();
        return PhenoPacket.newBuilder()
                // proband and the publication data
                .setSubject(Individual.newBuilder()
                        .setId(familyOrProbandId)
                        .setAgeAtCollection(Age.newBuilder().setAge(diseaseCase.getFamilyInfo().getAge()).build())
                        .setSex(DiseaseCaseToPhenoPacket.sex(diseaseCase.getFamilyInfo().getSex()))
                        .setTaxonomy(HOMO_SAPIENS)
                        .build())
                // phenotype (HPO) terms
                .addAllPhenotypes(diseaseCase.getPhenotypeList().stream()
                        .map(DiseaseCaseToPhenoPacket.phenotype(publication, metadata))
                        .collect(Collectors.toList()))
                // gene in question
                .addGenes(Gene.newBuilder()
                        .setId("ENTREZ:" + diseaseCase.getGene().getEntrezId())
                        .setSymbol(diseaseCase.getGene().getSymbol())
                        .build())
                // variants, genome assembly
                .addAllVariants(diseaseCase.getVariantList().stream()
                        .map(DiseaseCaseToPhenoPacket.hcaVariantToPhenoPacketVariant(familyOrProbandId))
                        .collect(Collectors.toList()))
                // disease
                .addDiseases(Disease.newBuilder()
                        .setTerm(ontologyClass(diseaseCase.getDisease().getDatabase() + ":" + diseaseCase.getDisease().getDiseaseId(), diseaseCase.getDisease().getDiseaseName()))
                        .build())
                // metadata - Biocurator ID, ontologies used
                .setMetaData(MetaData.newBuilder()
                        .setCreatedBy(diseaseCase.getBiocurator().getBiocuratorId())
                        .addAllResources(RESOURCES)
                        .build())
                .build();
    }

    public static void writeAsPhenopacket(Writer writer, PhenoPacket packet) throws IOException {
        writer.write(PhenoPacketFormat.toJson(packet));
    }

//    /**
//     * Convert given <code>phenoPacket</code> into HpoCaseAnnotator's {@link DiseaseCase} format
//     *
//     * @param phenoPacket {@link PhenoPacket} to be converted
//     * @return {@link DiseaseCase} with the data from <code>phenoPacket</code>
//     */
//    public static DiseaseCase phenopacketToDiseaseCase(PhenoPacket phenoPacket) {
//        // TODO - implement phenopacket to diseasecase conversion
//        return null;
//    }


    private static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }

    /**
     * HpoCaseAnnotator uses following resources:
     * <ul>
     * <li>Human Phenotype Ontology</li>
     * <li>Phenotype And Trait Ontology</li>
     * <li>Genotype Ontology</li>
     * <li>NCBI organismal classification</li>
     * <li>Evidence and Conclusion Ontology</li>
     * </ul>
     * <p>
     * Here the list of {@link Resource} objects representing these resources is generated.
     *
     * @return {@link List} of {@link Resource}s
     */
    private static List<Resource> makeResources() {
        return Arrays.asList(
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
                        .build()
        );
    }

    /**
     * This class contains functions for mapping data from {@link DiseaseCase} into {@link PhenoPacket} format.
     */
    private static class DiseaseCaseToPhenoPacket {

        private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToPhenoPacket.class);

        /**
         * @param sex {@link Sex} <code>sex</code>
         * @return {@link OntologyClass} representation of the <code>sex</code>
         */
        private static org.phenopackets.schema.v1.core.Sex sex(Sex sex) {
            switch (sex) {
                case MALE:
                    return org.phenopackets.schema.v1.core.Sex.MALE;
                case FEMALE:
                    return org.phenopackets.schema.v1.core.Sex.FEMALE;
                default:
                    return org.phenopackets.schema.v1.core.Sex.UNKNOWN_SEX;
            }
        }


        /**
         * @param publication
         * @return
         */
        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass, Phenotype> phenotype(Publication publication, String metadata) {
            return oc ->
                    Phenotype.newBuilder()
                            .setType(OntologyClass.newBuilder() // the primary HPO term
                                    .setId(oc.getId())
                                    .setLabel(oc.getLabel())
                                    .build())
                            .setNegated(oc.getNotObserved())
                            .addEvidence(Evidence.newBuilder()
                                    .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                                    .setReference(ExternalReference.newBuilder()
                                            .setId("PMID:" + publication.getPmid())
                                            // This is perhaps too much information
//                                            .setDescription(ModelUtils.getPublicationSummary(publication))
                                            .setDescription(metadata)
                                            .build())
                                    .build())
                            .build();
        }

        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> hcaVariantToPhenoPacketVariant(String familyOrProbandId) {
            return v -> Variant.newBuilder()
                    .setVcfAllele(VcfAllele.newBuilder()
                            .setId(hcaGenomeAssemblyToPhenoPacketGenomeAssembly(v.getVariantPosition().getGenomeAssembly()))
                            .setChr(v.getVariantPosition().getContig())
                            .setPos(v.getVariantPosition().getPos())
                            .setRef(v.getVariantPosition().getRefAllele())
                            .setAlt(v.getVariantPosition().getAltAllele())
                            .build())
                    .setGenotype(genotype(v.getGenotype()))
                    .build();
        }

        private static String hcaGenomeAssemblyToPhenoPacketGenomeAssembly(org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly genomeAssembly) {
            switch (genomeAssembly) {
                case GRCH_37:
                    return GenomeAssembly.GRCH_37.name();
                case GRCH_38:
                    return GenomeAssembly.GRCH_38.name();
                case UNKNOWN_GENOME_ASSEMBLY:
                case UNRECOGNIZED:
                    return GenomeAssembly.UNKNOWN_ASSEMBLY.name();
                default:
                    LOGGER.warn("Unknown genome assembly: {}", genomeAssembly);
                    return GenomeAssembly.UNKNOWN_ASSEMBLY.name();
            }
        }

        private static OntologyClass genotype(Genotype genotype) {
            switch (genotype) {
                case HEMIZYGOUS:
                    return HEMIZYGOUS;
                case HOMOZYGOUS_ALTERNATE:
                    return HOM_ALT;
                case HETEROZYGOUS:
                    return HET;
                case UNDEFINED:
                case HOMOZYGOUS_REFERENCE:
                case UNRECOGNIZED:
                default:
                    return OntologyClass.getDefaultInstance();
            }
        }
    }
}
