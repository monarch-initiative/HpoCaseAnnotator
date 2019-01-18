package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.*;

import java.io.BufferedReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


public class PhenoPacketCodecTest {


    @Test
    public void diseaseCaseToPhenopacket() {
        // arange
        DiseaseCase diseaseCase = TestResources.benMahmoud2013B3GLCT();
        String metadata = diseaseCase.getMetadata();

        // act
        final PhenoPacket packet = PhenoPacketCodec.diseaseCaseToPhenopacket(diseaseCase);

        // assert
        assertThat(packet, is(notNullValue()));
        final Individual subject = packet.getSubject();
        assertThat(subject.getId(), is("Tunisian patients"));
        assertThat(subject.getAgeAtCollection(), is(Age.newBuilder().setAge("P25Y").build()));
        assertThat(subject.getSex(), is(PhenoPacketCodec.MALE));

        final List<Phenotype> phenotypesList = subject.getPhenotypesList();
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
        assertThat(subject.getTaxonomy(), is(PhenoPacketCodec.HOMO_SAPIENS));

        final List<Gene> genesList = packet.getGenesList();
        assertThat(genesList.size(), is(1));
        assertThat(genesList.get(0), is(Gene.newBuilder()
                .setId("ENTREZ:145173")
                .setSymbol("B3GLCT")
                .build()));

        final List<Variant> variantsList = packet.getVariantsList();
        assertThat(variantsList.size(), is(1));
        Variant variant = variantsList.get(0);
        assertThat(variant.getGenomeAssembly(), is(GenomeAssembly.GRCH_37));
        assertThat(variant.getCoordinateSystem(), is(CoordinateSystem.ONE_BASED));
        assertThat(variant.getSequence(), is("13"));
        assertThat(variant.getPosition(), is(31843349));
        assertThat(variant.getDeletion(), is("A"));
        assertThat(variant.getInsertion(), is("G"));
        assertThat(variant.getSampleGenotypesCount(), is(1));
        assertThat(variant.getSampleGenotypesMap().get("Tunisian patients"), is(PhenoPacketCodec.HET));

        final List<Disease> diseasesList = packet.getDiseasesList();
        assertThat(diseasesList.size(), is(1));
        assertThat(diseasesList.get(0), is(Disease.newBuilder()
                .setId("OMIM:261540")
                .setLabel("PETERS-PLUS SYNDROME")
                .build()));

        final MetaData metaData = packet.getMetaData();
        assertThat(metaData.getCreatedBy(), is("HPO:ahegde"));
        assertThat(metaData.getResourcesList(), is(PhenoPacketCodec.RESOURCES));
    }

    @Test
    public void writePhenopacketToWriter() throws Exception {
        // arange
        final PhenoPacket packet = TestResources.rareDiseasePhenoPacket();
        StringWriter writer = new StringWriter();
        String expected;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(PhenoPacketCodecTest.class.getResource("examplePhenoPacket_v0.2.0.json").toURI()))) {
            expected = reader.lines().collect(Collectors.joining("\n"));
        }

        // act
        PhenoPacketCodec.writeAsPhenopacket(writer, packet);

        // assert
        assertThat(writer.toString(), is(equalTo(expected)));

    }

    @Test
    @Ignore // TODO - implement phenopacket to DiseaseCase parsing
    public void phenopacketToDiseaseCase() {
//        PhenoPacket packet = TestResources.rareDiseasePhenoPacket();
//        final DiseaseCase diseaseCase = PhenopacketCodec.phenopacketToDiseaseCase(packet);
//        assertThat(diseaseCase, is(notNullValue()));
    }
}