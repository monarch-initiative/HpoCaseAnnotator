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
import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.ontologyClass;

public class DiseaseCaseToPhenopacketCodecTest {

    private DiseaseCaseToPhenopacketCodec instance;

    @Before
    public void setUp() throws Exception {
        instance = new DiseaseCaseToPhenopacketCodec();
    }

    @Test
    public void diseaseCaseToPhenopacket() {
        // arrange
        DiseaseCase diseaseCase = TestResources.benMahmoud2013B3GLCT();
        String metadata = diseaseCase.getMetadata();

        // act
        final Phenopacket packet = instance.encode(diseaseCase);

        // assert
        assertThat(packet, is(notNullValue()));
        assertThat(packet.getId(), is("PMID:23954224-Ben_Mahmoud-2013-B3GLCT-Tunisian_patients"));
        assertThat(packet.getSubject(), is(Individual.newBuilder()
                .setId("Tunisian patients")
                .setAgeAtCollection(Age.newBuilder().setAge("P25Y").build())
                .setSex(Sex.MALE)
                .setTaxonomy(DiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                .build()));

        final List<PhenotypicFeature> phenotypesList = packet.getPhenotypicFeaturesList();
        assertThat(phenotypesList, hasSize(6));
        assertThat(phenotypesList, hasItems(PhenotypicFeature.newBuilder()
                        .setType(OntologyClass.newBuilder()
                                .setId("HP:0003498")
                                .setLabel("Disproportionate short stature")
                                .build())
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(OntologyClass.newBuilder()
                                        .setId("ECO:0000033")
                                        .setLabel("author statement supported by traceable reference")
                                        .build())
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:23954224")
                                        .setDescription(metadata)
                                        .build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(OntologyClass.newBuilder()
                                .setId("HP:0000268")
                                .setLabel("Dolichocephaly")
                                .build())
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(OntologyClass.newBuilder()
                                        .setId("ECO:0000033")
                                        .setLabel("author statement supported by traceable reference")
                                        .build())
                                .setReference(ExternalReference.newBuilder()
                                        .setId("PMID:23954224")
                                        .setDescription(metadata)
                                        .build())
                                .build())
                        .build()
        ));


        final List<Gene> genesList = packet.getGenesList();
        assertThat(genesList.size(), is(1));
        assertThat(genesList, hasItem(Gene.newBuilder()
                .setId("NCBIGene:145173")
                .setSymbol("B3GLCT")
                .build()));

        final List<Variant> variantsList = packet.getVariantsList();
        assertThat(variantsList, hasSize(1));
        assertThat(variantsList, hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly("GRCh37")
                        .setChr("13")
                        .setPos(31843349)
                        .setRef("A")
                        .setAlt("G")
                        .build())
                .setZygosity(DiseaseCaseToPhenopacketCodec.HET)
                .build()));


        final List<Disease> diseasesList = packet.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME"))
                .build()));

        final MetaData metaData = packet.getMetaData();
        assertThat(metaData, is(MetaData.newBuilder()
                .setSubmittedBy("HPO:ahegde")
                .setCreatedBy("Hpo Case Annotator")
                .addAllResources(DiseaseCaseToPhenopacketCodec.RESOURCES)
                .build()));
    }

    @Test
    public void diseaseCaseWithStructuralVariant() {
        // ------------  arrange  ------------
        DiseaseCase diseaseCase = TestResources.structural_beygo_2012_TCOF1_M18662();

        // ------------    act    ------------
        Phenopacket pp = instance.encode(diseaseCase);

        // ------------  assert  ------------
        assertThat(pp, is(notNullValue()));

        // id
        assertThat(pp.getId(), is("PMID:22712005-Beygo-2012-TCOF1-M18662"));
        // subject/individual
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("M18662")
                .setAgeAtCollection(Age.newBuilder().setAge("P26Y").build())
                .setSex(Sex.FEMALE)
                .setTaxonomy(DiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                .build()));
        // phenotypes
        List<PhenotypicFeature> phenos = pp.getPhenotypicFeaturesList();
        assertThat(phenos, hasSize(4));
        Evidence expectedEvidence = Evidence.newBuilder()
                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                .setReference(ExternalReference.newBuilder()
                        .setId("PMID:22712005")
                        .setDescription("Authors describe a proband M18662 with presence of TCS1")
                        .build())
                .build();
        assertThat(phenos, hasItems(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0011453", "Abnormality of the incus"))
                        .addEvidence(expectedEvidence)
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000405", "Conductive hearing impairment"))
                        .addEvidence(expectedEvidence)
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0025336", "Delayed ability to sit"))
                        .setNegated(true)
                        .addEvidence(expectedEvidence)
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000750", "Delayed speech and language development"))
                        .setNegated(true)
                        .addEvidence(expectedEvidence)
                        .build()));

        // genes
        List<Gene> genesList = pp.getGenesList();
        assertThat(genesList, hasSize(1));
        assertThat(genesList, hasItem(Gene.newBuilder()
                .setId("NCBIGene:6949")
                .setSymbol("TCOF1")
                .build()));

        // variants
        List<Variant> variants = pp.getVariantsList();
        assertThat(variants, hasSize(1));
        assertThat(variants, hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder()
                        .setGenomeAssembly("GRCh37")
                        .setChr("5")
                        .setPos(149741531)
                        .setRef("N")
                        .setAlt("<DEL>")
                        .setInfo("SVTYPE=DEL;END=149744897;CIPOS=-5,5;CIEND=-15,10")
                        .build())
                .setZygosity(ontologyClass("GENO:0000135", "heterozygous"))
                .build()));

        // diseases
        List<Disease> diseasesList = pp.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:154500", "TREACHER COLLINS SYNDROME 1; TCS1"))
                .build()));

        // metadata
        assertThat(pp.getMetaData(), is(MetaData.newBuilder()
                .setCreatedBy("Hpo Case Annotator")
                .setSubmittedBy("HPO:walterwhite")
                .addAllResources(DiseaseCaseToPhenopacketCodec.makeResources())
                .build()));
    }
}