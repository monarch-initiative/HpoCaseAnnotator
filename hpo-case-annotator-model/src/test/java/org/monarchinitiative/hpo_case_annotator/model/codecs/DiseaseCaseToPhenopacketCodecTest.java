package org.monarchinitiative.hpo_case_annotator.model.codecs;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.HET;
import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.ontologyClass;

public class DiseaseCaseToPhenopacketCodecTest {

    private DiseaseCaseToPhenopacketCodec instance;

    @Before
    public void setUp() {
        instance = new DiseaseCaseToPhenopacketCodec();
    }

    @Test
    public void diseaseCaseToPhenopacket() {
        // arrange
        DiseaseCase diseaseCase = TestResources.benMahmoud2013B3GLCT();

        // act
        Phenopacket pp = instance.encode(diseaseCase);

        // assert
        assertThat(pp, is(notNullValue()));
        assertThat(pp.getId(), is("PMID:23954224-Ben_Mahmoud-2013-B3GLCT-Tunisian_patients"));
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("Tunisian patients")
                .setAgeAtCollection(Age.newBuilder().setAge("P25Y").build())
                .setSex(Sex.MALE)
                .setTaxonomy(AbstractDiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                .build()));

        List<PhenotypicFeature> phenotypesList = pp.getPhenotypicFeaturesList();
        assertThat(phenotypesList, hasSize(6));
        assertThat(phenotypesList, hasItems(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0003498", "Disproportionate short stature"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0007957", "Corneal opacity"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000268", "Dolichocephaly"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000311", "Round face"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0011451", "Congenital microcephaly"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0004325", "Decreased body weight"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build()));


        List<Gene> genesList = pp.getGenesList();
        assertThat(genesList.size(), is(1));
        assertThat(genesList, hasItem(Gene.newBuilder()
                .setId("NCBIGene:145173")
                .setSymbol("B3GLCT")
                .build()));

        List<Variant> variantsList = pp.getVariantsList();
        assertThat(variantsList, hasSize(1));
        assertThat(variantsList, hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly("GRCh37")
                        .setChr("13")
                        .setPos(31843349)
                        .setRef("A")
                        .setAlt("G")
                        .build())
                .setZygosity(AbstractDiseaseCaseToPhenopacketCodec.HET)
                .build()));


        List<Disease> diseasesList = pp.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME"))
                .build()));

        assertThat(pp.getMetaData(), is(MetaData.newBuilder()
                .setCreated(pp.getMetaData().getCreated())
                .setSubmittedBy("HPO:ahegde")
                .setCreatedBy("Hpo Case Annotator")
                .setPhenopacketSchemaVersion("1.0.0-RC3")
                .addAllResources(AbstractDiseaseCaseToPhenopacketCodec.RESOURCES)
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:23954224")
                        .setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome")
                        .build())
                .build()));
    }

    @Test
    public void diseaseCaseWithStructuralVariantToPhenopacket() {
        // -- arrange
        DiseaseCase diseaseCase = TestResources.structural_beygo_2012_TCOF1_M18662();

        // -- act
        Phenopacket pp = instance.encode(diseaseCase);

        // -- assert
        // id
        assertThat(pp.getId(), is("PMID:22712005-Beygo-2012-TCOF1-M18662"));

        // subject
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("M18662")
                .setAgeAtCollection(Age.newBuilder().setAge("P26Y").build())
                .setSex(Sex.FEMALE)
                .setTaxonomy(AbstractDiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                .build()));

        // phenotypes
        assertThat(pp.getPhenotypicFeaturesList(), hasSize(4));
        assertThat(pp.getPhenotypicFeaturesList(), hasItems(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0011453", "Abnormality of the incus"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000405", "Conductive hearing impairment"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0025336", "Delayed ability to sit"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .setNegated(true)
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000750", "Delayed speech and language development"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(AbstractDiseaseCaseToPhenopacketCodec.TRACEABLE_AUTHOR_STATEMENT)
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:22712005")
                                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                        .build())
                                .build())
                        .setNegated(true)
                        .build()));

        // genes
        assertThat(pp.getGenesList(), hasSize(1));
        assertThat(pp.getGenesList(), hasItem(Gene.newBuilder()
                .setId("NCBIGene:6949")
                .setSymbol("TCOF1")
                .build()));

        // variants
        assertThat(pp.getVariantsList(), hasSize(1));
        assertThat(pp.getVariantsList(), hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly("GRCh37")
                        .setChr("5")
                        .setPos(149741531)
                        .setRef("N")
                        .setAlt("<DEL>")
                        .setInfo("SVTYPE=DEL;END=149744897;CIPOS=-5,5;CIEND=-15,10;IMPRECISE")
                        .build())
                .setZygosity(AbstractDiseaseCaseToPhenopacketCodec.HET)
                .build()));

        // diseases
        List<Disease> diseasesList = pp.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:154500", "TREACHER COLLINS SYNDROME 1; TCS1"))
                .build()));

        // metadata
        assertThat(pp.getMetaData(), is(MetaData.newBuilder()
                .setCreated(pp.getMetaData().getCreated())
                .setCreatedBy("Hpo Case Annotator")
                .setSubmittedBy("HPO:walterwhite")
                .addAllResources(DiseaseCaseToPhenopacketCodec.makeResources())
                .setPhenopacketSchemaVersion("1.0.0-RC3")
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:22712005")
                        .setDescription("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                        .build())
                .build()));
    }

    @Test
    public void diseaseCaseWithBreakendVariantToPhenopacket() {
        // -- arrange
        DiseaseCase diseaseCase = TestResources.structuralFictionalBreakend();

        // -- act
        Phenopacket pp = instance.encode(diseaseCase);

        // -- assert
        // id
        assertThat(pp.getId(), is("PMID:22712005-Beygo-2012-TCOF1-M18662"));

        // here we mainly need to test the variant
        assertThat(pp.getVariantsCount(), is(1));
        Variant variant = pp.getVariants(0);

        assertThat(variant.getAlleleCase(), is(Variant.AlleleCase.VCF_ALLELE));
        VcfAllele vcfAllele = variant.getVcfAllele();
        assertThat(vcfAllele.getGenomeAssembly(), is("GRCh37"));
        assertThat(vcfAllele.getChr(), is("9"));
        assertThat(vcfAllele.getPos(), is(133_359_000));
        assertThat(vcfAllele.getRef(), is("G"));
        assertThat(vcfAllele.getAlt(), is("G[13:32300000["));
        assertThat(vcfAllele.getInfo(), is("SVTYPE=BND;CIPOS=-5,-15;CIEND=10,20;IMPRECISE"));

        assertThat(variant.getZygosity(), is(HET));
    }
}