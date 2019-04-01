package org.monarchinitiative.hpo_case_annotator.model.codecs;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
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
        final Individual subject = packet.getSubject();
        assertThat(subject.getId(), is("Tunisian patients"));
        assertThat(subject.getAgeAtCollection(), is(Age.newBuilder().setAge("P25Y").build()));
        assertThat(subject.getSex(), is(Sex.MALE));

        final List<Phenotype> phenotypesList = packet.getPhenotypesList();
        assertThat(phenotypesList.size(), is(6));
        assertThat(phenotypesList, hasItem(Phenotype.newBuilder()
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
                .build()));
        assertThat(phenotypesList, hasItem(Phenotype.newBuilder()
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
                .build()));
        assertThat(subject.getTaxonomy(), is(DiseaseCaseToPhenopacketCodec.HOMO_SAPIENS));

        final List<Gene> genesList = packet.getGenesList();
        assertThat(genesList.size(), is(1));
        assertThat(genesList.get(0), is(Gene.newBuilder()
                .setId("ENTREZ:145173")
                .setSymbol("B3GLCT")
                .build()));

        final List<Variant> variantsList = packet.getVariantsList();
        assertThat(variantsList.size(), is(1));
        Variant variant = variantsList.get(0);

        assertTrue(variant.hasVcfAllele());
        VcfAllele va = variant.getVcfAllele();
        assertThat(va.getId(), is(GenomeAssembly.GRCH_37.name()));
        assertThat(va.getChr(), is("13"));
        assertThat(va.getPos(), is(31843349));
        assertThat(va.getRef(), is("A"));
        assertThat(va.getAlt(), is("G"));
        assertThat(variant.getGenotype(), is(DiseaseCaseToPhenopacketCodec.HET));

        final List<Disease> diseasesList = packet.getDiseasesList();
        assertThat(diseasesList.size(), is(1));
        assertThat(diseasesList.get(0), is(Disease.newBuilder()
                .setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME"))
                .build()));

        final MetaData metaData = packet.getMetaData();
        assertThat(metaData.getCreatedBy(), is("HPO:ahegde"));
        assertThat(metaData.getResourcesList(), is(DiseaseCaseToPhenopacketCodec.RESOURCES));
    }

}