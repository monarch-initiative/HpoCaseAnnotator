package org.monarchinitiative.hpo_case_annotator.export;

import com.google.protobuf.Timestamp;
import org.apache.commons.codec.digest.DigestUtils;
import org.monarchinitiative.hpo_case_annotator.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.core.utils.BreakendAltComposer;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.Gene;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Sex;
import org.phenopackets.schema.v1.core.Variant;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This codec converts {@link DiseaseCase} into {@link Phenopacket}. Phenopacket is meant to be used as an interchange
 * format between various programs.
 * <p>
 * Both conversions are lossy since neither format is a complete superset of the other.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
@SuppressWarnings("HttpUrlsUsage")
class DiseaseCaseToV1PhenopacketCodec implements Codec<DiseaseCase, Phenopacket> {

    // source https://bioportal.bioontology.org/ontologies/ECO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FECO_0000033&jump_to_nav=true
    static final OntologyClass TRACEABLE_AUTHOR_STATEMENT = OntologyClass.newBuilder()
            .setId("ECO:0000033")
            .setLabel("author statement supported by traceable reference")
            .build();
    static final OntologyClass HOMO_SAPIENS = OntologyClass.newBuilder()
            .setId("NCBITaxon:9606")
            .setLabel("Homo sapiens")
            .build();
    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000135
    static final OntologyClass HET = OntologyClass.newBuilder()
            .setId("GENO:0000135")
            .setLabel("heterozygous")
            .build();
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToV1PhenopacketCodec.class);
    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000136
    private static final OntologyClass HOM_ALT = OntologyClass.newBuilder()
            .setId("GENO:0000136")
            .setLabel("homozygous")
            .build();
    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000134
    private static final OntologyClass HEMIZYGOUS = OntologyClass.newBuilder()
            .setId("GENO:0000134")
            .setLabel("hemizygous")
            .build();

    static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
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

    private static OntologyClass hcaGenotypeToPhenopacketZygosity(Genotype genotype) {
        switch (genotype) {
            case HETEROZYGOUS:
                return OntologyClass.newBuilder()
                        .setId("GENO:0000135")
                        .setLabel("heterozygous")
                        .build();
            case HOMOZYGOUS_ALTERNATE:
                return DiseaseCaseToV1PhenopacketCodec.HOM_ALT;
            case HEMIZYGOUS:
                return DiseaseCaseToV1PhenopacketCodec.HEMIZYGOUS;
            case HOMOZYGOUS_REFERENCE:
            case UNDEFINED:
            default:
                return OntologyClass.getDefaultInstance();
        }
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass, PhenotypicFeature> hcaPhenotypeToPhenopacketPhenotype(Publication publication) {
        return oc -> PhenotypicFeature.newBuilder()
                .setType(OntologyClass.newBuilder()
                        .setId(oc.getId())
                        .setLabel(oc.getLabel())
                        .build())
                .setNegated(oc.getNotObserved())
                .addEvidence(Evidence.newBuilder()
                        .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                        .setReference(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", publication.getPmid()))
                                .setDescription(publication.getTitle())
                                .build())
                        .build())
                .build();
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> hcaVariantToPhenopacketVariant() {
        return v -> {
            VariantPosition vp = v.getVariantPosition();
            List<String> info = new ArrayList<>();
            String alt = vp.getAltAllele();
            if (v.getVariantClass().equals("structural")) {
                info.add(String.format("SVTYPE=%s", v.getSvType().name()));
                if (vp.getCiBeginOne() != 0 && vp.getCiBeginTwo() != 0) { // CIPOS
                    info.add(String.format("CIPOS=%d,%d", vp.getCiBeginOne(), vp.getCiBeginTwo()));
                }
                if (vp.getCiEndOne() != 0 && vp.getCiEndTwo() != 0)
                    info.add(String.format("CIEND=%d,%d", vp.getCiEndOne(), vp.getCiEndTwo()));
                if (v.getSvType().equals(StructuralType.BND)) {
                    // breakend is a special animal in the yard
                    alt = BreakendAltComposer.composeBreakendAltAllele(vp.getContig2(), String.valueOf(vp.getPos2()), vp.getRefAllele(),
                            vp.getContigDirection().equals(VariantPosition.Direction.FWD),
                            vp.getContig2Direction().equals(VariantPosition.Direction.FWD),
                            vp.getAltAllele());
                } else {
                    // populate the info field if the variant class is structural
                    info.add(String.format("END=%d", vp.getPos2()));
                }
                if (v.getImprecise()) {
                    info.add("IMPRECISE");
                }
            }

            String id = String.join("-", vp.getContig(), String.valueOf(vp.getPos()), vp.getRefAllele(), vp.getAltAllele());
            String digest = DigestUtils.md5Hex(id);
            return Variant.newBuilder()
                    .setVcfAllele(VcfAllele.newBuilder()
                            .setGenomeAssembly(hcaGenomeAssemblyToPhenopacketGenomeAssembly(vp.getGenomeAssembly()))
                            .setId(digest)
                            .setChr(vp.getContig())
                            .setPos(vp.getPos())
                            .setRef(vp.getRefAllele())
                            .setAlt(alt)
                            .setInfo(String.join(";", info))
                            .build())
                    .setZygosity(hcaGenotypeToPhenopacketZygosity(v.getGenotype()))
                    .build();
        };
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
        String phenopacketVersion = "1.0.0";
        return Phenopacket.newBuilder()
                // id
                .setId(Utils.getPhenopacketIdFor(data))
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
                        .setTerm(OntologyClass.newBuilder()
                                .setId(data.getDisease().getDatabase() + ":" + data.getDisease().getDiseaseId())
                                .setLabel(data.getDisease().getDiseaseName())
                                .build())
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
        throw new UnsupportedOperationException("Conversion from Phenopacket to Disease case is not supported");
    }

}
