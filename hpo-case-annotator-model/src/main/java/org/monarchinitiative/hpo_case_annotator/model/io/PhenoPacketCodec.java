package org.monarchinitiative.hpo_case_annotator.model.io;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.proto.Sex;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.*;

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

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

    public static final OntologyClass FEMALE = ontologyClass("PATO:0000383", "female");

    public static final OntologyClass MALE = ontologyClass("PATO:0000384", "male");

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
        GenomeAssembly assembly = DiseaseCaseToPhenoPacket.genomeAssembly(diseaseCase.getGenomeBuild());
        String familyOrProbandId = diseaseCase.getFamilyInfo().getFamilyOrProbandId();
        Publication publication = diseaseCase.getPublication();
        return PhenoPacket.newBuilder()
                // proband, HPO terms, and the publication data
                .setSubject(Individual.newBuilder()
                        .setId(familyOrProbandId)
                        .setAgeAtCollection(Age.newBuilder().setAge(diseaseCase.getFamilyInfo().getAge()).build())
                        .setSex(DiseaseCaseToPhenoPacket.sex(diseaseCase.getFamilyInfo().getSex()))
                        .addAllPhenotypes(diseaseCase.getPhenotypeList().stream()
                                .map(DiseaseCaseToPhenoPacket.phenotype(publication))
                                .collect(Collectors.toList()))
                        .setTaxonomy(HOMO_SAPIENS)
                        .build())
                // gene in question
                .addGenes(Gene.newBuilder()
                        .setId("ENTREZ:" + String.valueOf(diseaseCase.getGene().getEntrezId()))
                        .setSymbol(diseaseCase.getGene().getSymbol())
                        .build())
                // variants, genome assembly
                .addAllVariants(diseaseCase.getVariantList().stream()
                        .map(DiseaseCaseToPhenoPacket.variant(assembly, familyOrProbandId))
                        .collect(Collectors.toList()))
                // disease
                .addDiseases(Disease.newBuilder()
                        .setId(diseaseCase.getDisease().getDatabase() + ":" + diseaseCase.getDisease().getDiseaseId())
                        .setLabel(diseaseCase.getDisease().getDiseaseName())
                        .build())
                // metadata - Biocurator ID, ontologies used
                .setMetaData(MetaData.newBuilder()
                        .setCreatedBy(diseaseCase.getBiocurator().getBiocuratorId())
                        .addAllResources(RESOURCES)
                        .build())
                .build();
    }

    public static void writeAsPhenopacket(Writer writer, PhenoPacket packet) throws IOException {
        String jsonForm = JSON_PRINTER.print(packet);
        writer.write(jsonForm);
    }

    public static DiseaseCase phenopacketToDiseaseCase(PhenoPacket phenoPacket) {
        return null;
    }


    private static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }

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

    private static class DiseaseCaseToPhenoPacket {

        private static OntologyClass sex(Sex sex) {
            switch (sex) {
                case MALE:
                    return MALE;
                case FEMALE:
                    return FEMALE;
                default:
                    return OntologyClass.getDefaultInstance();
            }
        }


        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass, Phenotype> phenotype(Publication publication) {
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
                                            .build())
                                    .build())
                            .build();
        }

        private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> variant(GenomeAssembly assembly, String familyOrProbandId) {
            return v -> Variant.newBuilder()
                    .setGenomeAssembly(assembly)
                    .setSequence(v.getContig())
                    .setCoordinateSystem(CoordinateSystem.ONE_BASED) // We are using VCF-style numbering in HpoCaseAnnotator
                    .setPosition(v.getPos())
                    .setDeletion(v.getRefAllele())
                    .setInsertion(v.getAltAllele())
                    .putSampleGenotypes(familyOrProbandId, genotype(v.getGenotype()))
                    .build();
        }

        private static GenomeAssembly genomeAssembly(String genomeBuild) {
            switch (genomeBuild.toUpperCase()) {
                case "HG19":
                case "GRCH37":
                    return GenomeAssembly.HG_19;
                case "HG38":
                case "GRCH38":
                    return GenomeAssembly.HG_38;
                case "HG18":
                case "NCBI36":
                    return GenomeAssembly.HG_18;
                default:
                    return GenomeAssembly.UNRECOGNIZED;
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
