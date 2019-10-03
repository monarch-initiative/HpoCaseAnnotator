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
    public void setUp() {
        instance = new DiseaseCaseToPhenopacketCodec();
    }

    @Test
    public void diseaseCaseToPhenopacket() {
        // arrange
        DiseaseCase diseaseCase = TestResources.benMahmoud2013B3GLCT();
        String title = diseaseCase.getPublication().getTitle();

        // act
        final Phenopacket packet = instance.encode(diseaseCase);

        // assert
        assertThat(packet, is(notNullValue()));
        assertThat(packet.getId(), is("PMID:23954224-Ben_Mahmoud-2013-B3GLCT-Tunisian_patients"));
        assertThat(packet.getSubject(), is(Individual.newBuilder()
                .setId("Tunisian patients")
                .setAgeAtCollection(Age.newBuilder().setAge("P25Y").build())
                .setSex(Sex.MALE)
                .setTaxonomy(AbstractDiseaseCaseToPhenopacketCodec.HOMO_SAPIENS)
                .build()));

        final List<PhenotypicFeature> phenotypesList = packet.getPhenotypicFeaturesList();
        assertThat(phenotypesList, hasSize(6));
        assertThat(phenotypesList, hasItems(PhenotypicFeature.newBuilder()
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
                .setZygosity(AbstractDiseaseCaseToPhenopacketCodec.HET)
                .build()));


        final List<Disease> diseasesList = packet.getDiseasesList();
        assertThat(diseasesList, hasSize(1));
        assertThat(diseasesList, hasItem(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME"))
                .build()));

        assertThat(packet.getMetaData(), is(MetaData.newBuilder()
                .setSubmittedBy("HPO:ahegde")
                .setCreatedBy("Hpo Case Annotator v1.0.12-SNAPSHOT")
                .setPhenopacketSchemaVersion("1.0.0-RC3")
                .addAllResources(AbstractDiseaseCaseToPhenopacketCodec.RESOURCES)
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:23954224")
                        .setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome")
                        .build())
                .build()));
    }
}