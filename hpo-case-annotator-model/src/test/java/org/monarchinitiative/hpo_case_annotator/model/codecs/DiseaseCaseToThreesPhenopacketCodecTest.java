package org.monarchinitiative.hpo_case_annotator.model.codecs;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.monarchinitiative.hpo_case_annotator.model.test_resources.PhenoPacketTestUtil.ontologyClass;

public class DiseaseCaseToThreesPhenopacketCodecTest {

    private DiseaseCaseToThreesPhenopacketCodec instance;

    @BeforeEach
    public void setUp() {
        instance = new DiseaseCaseToThreesPhenopacketCodec();
    }

    @Test
    public void encodeDiseaseCase() {
        DiseaseCase dc = TestResources.benMahmoud2013B3GLCT();

        Phenopacket pp = instance.encode(dc);
        // ID
        assertThat(pp.getId(), is("Ben_Mahmoud-2013-23954224-B3GLCT-Tunisian_patients"));

        // SUBJECT
        assertThat(pp.getSubject(), is(Individual.newBuilder()
                .setId("Tunisian patients")
                .setSex(Sex.MALE)
                .setTaxonomy(ontologyClass("NCBITaxon:9606", "Homo sapiens"))
                .build()));

        // PHENOTYPES
        assertThat(pp.getPhenotypicFeaturesList(), hasSize(6));
        assertThat(pp.getPhenotypicFeaturesList(), hasItems(
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

        // GENES
        assertThat(pp.getGenesList(), hasSize(1));
        assertThat(pp.getGenesList(), hasItem(Gene.newBuilder().setId("ENTREZ:145173").setSymbol("B3GLCT").build()));

        // VARIANTS
        assertThat(pp.getVariantsList(), hasSize(1));
        assertThat(pp.getVariantsList(), hasItem(Variant.newBuilder()
                .setVcfAllele(VcfAllele.newBuilder().setGenomeAssembly("GRCh37").setChr("13").setPos(31843349).setRef("A").setAlt("G").setInfo("VCLASS=splicing;PATHOMECHANISM=splicing|3ss|disrupted;CONSEQUENCE=Exon skipping").build())
                .setZygosity(ontologyClass("GENO:0000135", "heterozygous"))
                .build()));

        // DISEASES
        assertThat(pp.getDiseasesList(), is(Collections.singletonList(Disease.newBuilder().setTerm(ontologyClass("OMIM:261540", "PETERS-PLUS SYNDROME")).build())));

        // METADATA
        MetaData md = pp.getMetaData();
        assertThat(md, is(MetaData.newBuilder()
                .setCreated(md.getCreated()) // cannot test this properly since timestamp is being updated all the time
                .setCreatedBy("HPO:ahegde")
                .setSubmittedBy("Hpo Case Annotator")
                .setPhenopacketSchemaVersion("1.0.0")
                .addAllResources(AbstractDiseaseCaseToPhenopacketCodec.makeResources())
                .addExternalReferences(ExternalReference.newBuilder().setId("PMID:23954224").setDescription("First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome").build())
                .build()));
    }

    @Test
    public void decodeDiseaseCase() {
        // nothing to test here right now
    }
}