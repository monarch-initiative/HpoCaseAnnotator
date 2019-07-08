package org.monarchinitiative.hpo_case_annotator.model.codecs;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This codec converts {@link DiseaseCase} into {@link Phenopacket}. Phenopacket is meant to be used as an interchange
 * format between various programs.
 * <p>
 * Both conversions are lossy since neither format is a superset of the other.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class DiseaseCaseToPhenopacketCodec implements Codec<DiseaseCase, Phenopacket> {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToPhenopacketCodec.class);

    DiseaseCaseToPhenopacketCodec() {
        // package-private no-op
    }

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
    static List<Resource> makeResources() {
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
                        .build(),
                Resource.newBuilder()
                        .setId("omim")
                        .setName("Online Mendelian Inheritance in Man")
                        .setNamespacePrefix("OMIM")
                        .setUrl("https://www.omim.org")
                        .build()
        );
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
        String metadata = data.getMetadata();
        return Phenopacket.newBuilder()
                .setId(Codecs.getPhenopacketIdFor(data))
                // proband and the publication data
                .setSubject(Individual.newBuilder()
                        .setId(familyOrProbandId)
                        .setAgeAtCollection(Age.newBuilder().setAge(data.getFamilyInfo().getAge()).build())
                        .setSex(DiseaseCaseToPhenopacket.sex(data.getFamilyInfo().getSex()))
                        .setTaxonomy(HOMO_SAPIENS)
                        .build())
                // phenotype (HPO) terms
                .addAllPhenotypicFeatures(data.getPhenotypeList().stream()
                        .map(DiseaseCaseToPhenopacket.phenotype(publication, metadata))
                        .collect(Collectors.toList()))
                // gene in question
                .addGenes(Gene.newBuilder()
                        .setId("NCBIGene:" + data.getGene().getEntrezId())
                        .setSymbol(data.getGene().getSymbol())
                        .build())
                // variants, genome assembly
                .addAllVariants(data.getVariantList().stream()
                        .map(DiseaseCaseToPhenopacket.hcaVariantToPhenopacketVariant(familyOrProbandId))
                        .collect(Collectors.toList()))
                // disease
                .addDiseases(Disease.newBuilder()
                        .setTerm(ontologyClass(data.getDisease().getDatabase() + ":" + data.getDisease().getDiseaseId(), data.getDisease().getDiseaseName()))
                        .build())
                // metadata - Biocurator ID, ontologies used
                .setMetaData(MetaData.newBuilder()
                        .setCreatedBy(data.getSoftwareVersion())
                        .setSubmittedBy(data.getBiocurator().getBiocuratorId())
                        .addAllResources(RESOURCES)
                        .build())
                .build();
    }


    @Override
    public DiseaseCase decode(Phenopacket data) {
        // TODO - implement phenopacket to diseasecase conversion
        LOGGER.warn("Not yet supported");
        return null;
    }

    /**
     * This class contains functions for mapping data from {@link DiseaseCase} into {@link Phenopacket} format.
     */
    private static class DiseaseCaseToPhenopacket {

        private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToPhenopacketCodec.class);

        /**
         * @param sex {@link org.monarchinitiative.hpo_case_annotator.model.proto.Sex} <code>sex</code>
         * @return {@link OntologyClass} representation of the <code>sex</code>
         */
        private static org.phenopackets.schema.v1.core.Sex sex(org.monarchinitiative.hpo_case_annotator.model.proto.Sex sex) {
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
        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass, PhenotypicFeature> phenotype(Publication publication, String metadata) {
            return oc ->
                    PhenotypicFeature.newBuilder()
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

        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> hcaVariantToPhenopacketVariant(String familyOrProbandId) {
            return v -> Variant.newBuilder()
                    .setVcfAllele(VcfAllele.newBuilder()
                            .setGenomeAssembly(hcaGenomeAssemblyToPhenopacketGenomeAssembly(v.getVariantPosition().getGenomeAssembly()))
                            //.setId()
                            .setChr(v.getVariantPosition().getContig())
                            .setPos(v.getVariantPosition().getPos())
                            .setRef(v.getVariantPosition().getRefAllele())
                            .setAlt(v.getVariantPosition().getAltAllele())

                            .build())
                    .setZygosity(genotype(v.getGenotype()))
                    .build();
        }

        private static String hcaGenomeAssemblyToPhenopacketGenomeAssembly(org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly genomeAssembly) {
            switch (genomeAssembly) {
                case GRCH_37:
                    return "GRCh37";
                case GRCH_38:
                    return "GRCh38";
                default:
                    LOGGER.warn("Unknown genome assembly: {}", genomeAssembly);
                case UNKNOWN_GENOME_ASSEMBLY:
                case UNRECOGNIZED:
                    return "UNKNOWN";
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
