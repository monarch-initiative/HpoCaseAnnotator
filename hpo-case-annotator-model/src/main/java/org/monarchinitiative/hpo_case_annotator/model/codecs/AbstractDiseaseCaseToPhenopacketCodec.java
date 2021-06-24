package org.monarchinitiative.hpo_case_annotator.model.codecs;

import org.apache.commons.codec.digest.DigestUtils;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Sex;
import org.phenopackets.schema.v1.core.Variant;
import org.phenopackets.schema.v1.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static org.monarchinitiative.hpo_case_annotator.model.utils.BreakendAltComposer.composeBreakendAltAllele;

/**
 * <p>
 * Superclass of {@link Codec}s for convertion of {@link DiseaseCase} into a {@link Phenopacket}.
 * </p>
 * <p>
 * The extending class needs to implement {@link Codec} methods using constants and mapping functions defined here.
 * The implementations might be slightly different based on the purpose of the particular codec. (e.g.
 * {@link DiseaseCaseToThreesPhenopacketCodec} also stores certain splicing-related data into within the INFO field).
 * </p>
 */
public abstract class AbstractDiseaseCaseToPhenopacketCodec implements Codec<DiseaseCase, Phenopacket> {

    // source https://bioportal.bioontology.org/ontologies/ECO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FECO_0000033&jump_to_nav=true
    static final OntologyClass TRACEABLE_AUTHOR_STATEMENT = ontologyClass("ECO:0000033", "author statement supported by traceable reference");

    static final OntologyClass HOMO_SAPIENS = ontologyClass("NCBITaxon:9606", "Homo sapiens");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000135
    static final OntologyClass HET = ontologyClass("GENO:0000135", "heterozygous");

    static final List<Resource> RESOURCES = makeResources();

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000136
    private static final OntologyClass HOM_ALT = ontologyClass("GENO:0000136", "homozygous");

    // source https://bioportal.bioontology.org/ontologies/GENO/?p=classes&conceptid=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FGENO_0000134
    private static final OntologyClass HEMIZYGOUS = ontologyClass("GENO:0000134", "hemizygous");

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDiseaseCaseToPhenopacketCodec.class);

    final String phenopacketVersion;

    AbstractDiseaseCaseToPhenopacketCodec() {
        Properties properties = new Properties();
        try (InputStream is = DiseaseCaseToThreesPhenopacketCodec.class.getResourceAsStream("phenopacket_version.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.warn("Unable to read Phenopacket version, using 'N/A'");
        }

        phenopacketVersion = properties.getProperty("phenopacket.version", "N/A");
    }

    static OntologyClass ontologyClass(String id, String label) {
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

    static Sex hcaSexToPhenopacketSex(org.monarchinitiative.hpo_case_annotator.model.proto.Sex sex) {
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

    static String hcaGenomeAssemblyToPhenopacketGenomeAssembly(GenomeAssembly assembly) {
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

    static OntologyClass hcaGenotypeToPhenopacketZygosity(Genotype genotype) {
        switch (genotype) {
            case HETEROZYGOUS:
                return AbstractDiseaseCaseToPhenopacketCodec.HET;
            case HOMOZYGOUS_ALTERNATE:
                return AbstractDiseaseCaseToPhenopacketCodec.HOM_ALT;
            case HEMIZYGOUS:
                return AbstractDiseaseCaseToPhenopacketCodec.HEMIZYGOUS;
            case HOMOZYGOUS_REFERENCE:
            case UNDEFINED:
            default:
                return OntologyClass.getDefaultInstance();
        }
    }

    static Function<org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass, PhenotypicFeature> hcaPhenotypeToPhenopacketPhenotype(Publication publication) {
        return oc -> PhenotypicFeature.newBuilder()
                .setType(ontologyClass(oc.getId(), oc.getLabel()))
                .setNegated(oc.getNotObserved())
                .addEvidence(Evidence.newBuilder()
                        .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                        .setReference(ExternalReference.newBuilder()
                                .setId(String.format("PMID:%s", publication.getPmid()))
                                .setDescription(publication.getTitle())
                                .build())
                        .build())
                .build();
    }

    static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> hcaVariantToPhenopacketVariant() {
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
                    alt = composeBreakendAltAllele(vp.getContig2(), String.valueOf(vp.getPos2()), vp.getRefAllele(),
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

}
