package org.monarchinitiative.hpo_case_annotator.model.codecs;


import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.ontologyClass;

public class DiseaseCaseToThreesPhenopacketCodecTest {

    private DiseaseCaseToThreesPhenopacketCodec instance;

    @Before
    public void setUp() throws Exception {
        instance = new DiseaseCaseToThreesPhenopacketCodec();
    }

    @Test
    public void encodeDiseaseCase() {
        DiseaseCase dc = TestResources.benMahmoud2013B3GLCT();

        Phenopacket pp = instance.encode(dc);
        // ID
        assertThat(pp.getId(), is("PMID:23954224-Ben_Mahmoud-2013-B3GLCT-Tunisian_patients"));

        // SUBJECT
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("Tunisian patients")
                .setDatasetId("3S")
                .setSex(Sex.MALE)
                .setTaxonomy(ontologyClass("NCBITaxon:9606", "Homo sapiens"))
                .build()));

        // PHENOTYPES
        List<PhenotypicFeature> expectedPhenotypes = Arrays.asList(
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0003498", "Disproportionate short stature"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0007957", "Corneal opacity"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000268", "Dolichocephaly"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0000311", "Round face"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0011451", "Congenital microcephaly"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build(),
                PhenotypicFeature.newBuilder()
                        .setType(ontologyClass("HP:0004325", "Decreased body weight"))
                        .addEvidence(Evidence.newBuilder()
                                .setEvidenceCode(ontologyClass("ECO:0000033", "author statement supported by traceable reference"))
                                .setReference(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                                .build())
                        .build()
        );
        assertThat(pp.getPhenotypicFeaturesList(), is(expectedPhenotypes));

        // GENES
        assertThat(pp.getGenesList(), is(Collections.singletonList(Gene.newBuilder().setId("ENTREZ:145173").setSymbol("B3GLCT").build())));

        // VARIANTS
        assertThat(pp.getVariantsList(), is(Collections.singletonList(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder().setGenomeAssembly("GRCh37").setChr("13").setPos(31843349).setRef("A").setAlt("G").setInfo("VCLASS=splicing;PATHOMECHANISM=splicing|3ss|disrupted;CONSEQUENCE=Exon skipping").build())
                .setZygosity(ontologyClass("GENO:0000135", "heterozygous"))
                .build())));

        // DISEASES
        assertThat(pp.getDiseasesList(), is(Collections.singletonList(Disease.newBuilder().setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME")).build())));

        // METADATA
        MetaData md = pp.getMetaData();
        // cannot test md.getCreated(), because Timestamp is updated all the time
        assertThat(md.getCreatedBy(), is("HPO:ahegde"));
        assertThat(md.getSubmittedBy(), is("Hpo Case Annotator"));
        assertThat(md.getResourcesList(), is(DiseaseCaseToPhenopacketCodec.makeResources()));
        assertThat(md.getExternalReferencesList(), is(Collections.singletonList(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())));

    }

    @Test
    public void decodeDiseaseCase() {
        // nothing to test here right now
    }
}