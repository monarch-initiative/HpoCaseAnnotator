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
import static org.junit.Assert.assertTrue;
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
                .setCreatedBy("Hpo Case Annotator v1.0.12-SNAPSHOT")
                .addAllResources(DiseaseCaseToPhenopacketCodec.RESOURCES)
        .build()));
    }

}