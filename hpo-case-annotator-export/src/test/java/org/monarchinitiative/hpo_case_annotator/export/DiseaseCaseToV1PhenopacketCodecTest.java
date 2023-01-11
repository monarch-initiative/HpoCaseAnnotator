package org.monarchinitiative.hpo_case_annotator.export;

import com.google.protobuf.Message;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.monarchinitiative.hpo_case_annotator.export.DiseaseCaseToV1PhenopacketCodec.*;

@SuppressWarnings("HttpUrlsUsage")
public class DiseaseCaseToV1PhenopacketCodecTest {

    private DiseaseCaseToV1PhenopacketCodec instance;

    @BeforeEach
    public void setUp() {
        instance = DiseaseCaseToV1PhenopacketCodec.instance();
    }

    @Test
    public void diseaseCaseToPhenopacket() {
        // arrange
        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();

        // act
        Message message = instance.encode(diseaseCase);
        assertThat(message, is(instanceOf(Phenopacket.class)));
        Phenopacket pp = (Phenopacket) message;

        // assert
        assertThat(pp, is(notNullValue()));
        assertThat(pp.getId(), is("Beygo-2012-22712005-CFTR-FAM:001"));
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("FAM:001")
                .setAgeAtCollection(Age.newBuilder().setAge("P10Y5M4D").build())
                .setSex(Sex.MALE)
                .setTaxonomy(HOMO_SAPIENS)
                .build()));

        List<PhenotypicFeature> phenotypesList = pp.getPhenotypicFeaturesList();
        assertThat(phenotypesList, hasSize(2));
        assertThat(phenotypesList, hasItems(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:1234567", "Some feature"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:9876543", "Other feature"))
                        .setNegated(true)
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build()));


        List<Gene> genesList = pp.getGenesList();
        assertThat(genesList.size(), is(1));
        assertThat(genesList, hasItem(Gene.newBuilder()
                .setId("NCBIGene:1080")
                .setSymbol("CFTR")
                .build()));

        List<Variant> variantsList = pp.getVariantsList();
        assertThat(variantsList, hasSize(5));
        assertThat(variantsList, hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly("GRCh37")
                        .setChr("9")
                        .setPos(123737057)
                        .setId("a970e383c798532733d37a1b23c530d4")
                        .setRef("C")
                        .setAlt("A")
                        .build())
                .setZygosity(OntologyClass.newBuilder()
                        .setId("GENO:0000135")
                        .setLabel("heterozygous")
                        .build())
                .build()));


        List<Disease> diseasesList = pp.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:219700", "CYSTIC FIBROSIS; CF"))
                .build()));

        assertThat(pp.getMetaData(), is(MetaData.newBuilder()
                .setCreated(pp.getMetaData().getCreated())
                .setSubmittedBy("HPO:walterwhite")
                .setCreatedBy("Hpo Case Annotator")
                .setPhenopacketSchemaVersion("1.0.0")
                .addAllResources(RESOURCES)
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:22712005")
                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                        .build())
                .build()));
    }

    @Test
    public void diseaseCaseWithStructuralVariantToPhenopacket() {
        // -- arrange
        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();

        // -- act
        Message message = instance.encode(diseaseCase);
        assertThat(message, is(instanceOf(Phenopacket.class)));
        Phenopacket pp = (Phenopacket) message;

        // -- assert
        // id
        assertThat(pp.getId(), is("Beygo-2012-22712005-CFTR-FAM:001"));

        // subject
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("FAM:001")
                .setAgeAtCollection(Age.newBuilder().setAge("P10Y5M4D").build())
                .setSex(Sex.MALE)
                .setTaxonomy(OntologyClass.newBuilder()
                        .setId("NCBITaxon:9606")
                        .setLabel("Homo sapiens")
                        .build())
                .build()));

        // phenotypes
        assertThat(pp.getPhenotypicFeaturesList(), hasSize(2));
        assertThat(pp.getPhenotypicFeaturesList(), hasItems(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:1234567", "Some feature"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:9876543", "Other feature"))
                        .setNegated(true)
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build()));

        // genes
        assertThat(pp.getGenesList(), hasSize(1));
        assertThat(pp.getGenesList(), hasItem(Gene.newBuilder()
                .setId("NCBIGene:1080")
                .setSymbol("CFTR")
                .build()));

        // variants
        assertThat(pp.getVariantsList(), hasSize(5));
        Variant variant = pp.getVariantsList().get(3);
        assertThat(variant.getZygosity(), equalTo(HET));
        VcfAllele vcfAllele = variant.getVcfAllele();
        List<String> info = Arrays.asList(vcfAllele.getInfo().split(";"));
        assertThat(info, hasItems("SVTYPE=DEL", "END=149744897", "CIPOS=-5,5", "CIEND=-15,10", "IMPRECISE"));
        assertThat(vcfAllele.getGenomeAssembly(), equalTo("GRCh37"));
        assertThat(vcfAllele.getChr(), equalTo("5"));
        assertThat(vcfAllele.getPos(), equalTo(149741531));
        assertThat(vcfAllele.getRef(), equalTo("N"));
        assertThat(vcfAllele.getAlt(), equalTo("DEL"));

        // diseases
        List<Disease> diseasesList = pp.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:219700", "CYSTIC FIBROSIS; CF"))
                .build()));

        // metadata
        assertThat(pp.getMetaData(), is(MetaData.newBuilder()
                .setCreated(pp.getMetaData().getCreated())
                .setCreatedBy("Hpo Case Annotator")
                .setSubmittedBy("HPO:walterwhite")
                .addAllResources(RESOURCES)
                .setPhenopacketSchemaVersion("1.0.0")
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:22712005")
                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                        .build())
                .build()));
    }

    @Test
    public void diseaseCaseWithBreakendVariantToPhenopacket() {
        // -- arrange
        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();

        // -- act
        Message message = instance.encode(diseaseCase);
        assertThat(message, is(instanceOf(Phenopacket.class)));
        Phenopacket pp = (Phenopacket) message;

        // -- assert
        // id
        assertThat(pp.getId(), is("Beygo-2012-22712005-CFTR-FAM:001"));

        // here we mainly need to test the variant
        assertThat(pp.getVariantsCount(), is(5));
        Variant variant = pp.getVariants(4);

        assertThat(variant.getAlleleCase(), is(Variant.AlleleCase.VCF_ALLELE));
        VcfAllele vcfAllele = variant.getVcfAllele();
        assertThat(vcfAllele.getGenomeAssembly(), is("GRCh37"));
        assertThat(vcfAllele.getChr(), is("9"));
        assertThat(vcfAllele.getId(), is("3e80c92a7e2c901c7f3c17320bebbc54"));
        assertThat(vcfAllele.getPos(), is(133_359_000));
        assertThat(vcfAllele.getRef(), is("G"));
        assertThat(vcfAllele.getAlt(), is("G[13:32300000["));
        List<String> infoFields = Arrays.asList(vcfAllele.getInfo().split(";"));
        assertThat(infoFields, hasItems("IMPRECISE", "SVTYPE=BND", "CIPOS=-5,-15", "CIEND=10,20"));

        MatcherAssert.assertThat(variant.getZygosity(), Matchers.is(HET));
    }
}