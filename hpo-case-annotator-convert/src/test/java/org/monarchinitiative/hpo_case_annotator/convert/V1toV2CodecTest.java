package org.monarchinitiative.hpo_case_annotator.convert;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.MendelianVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.time.Period;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class V1toV2CodecTest {

    private static final GenomicAssembly GRCH37 = GenomicAssemblies.GRCh37p13();

    @Test
    public void encodeDiseaseCase() throws ModelTransformationException {
        DiseaseCase comprehensiveCase = TestData.V1.comprehensiveCase();
        V1toV2Codec instance = V1toV2Codec.getInstance();

        Study study = instance.encode(comprehensiveCase);

        // Test publication
        assertThat(study.publication(), equalTo(Publication.of(
                List.of("Beygó J", "Buiting K", "Seland S", "Lüdecke HJ", "Hehr U", "Lich C", "Prager B", "Lohmann DR", "Wieczorek D"),
                "First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome",
                "Mol Syndromol",
                2012,
                "2(2)",
                "53-59",
                "22712005")));

        // Test metadata
        StudyMetadata metadata = study.studyMetadata();
        String freeText = """
                Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).
                                
                The 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA .""";
        assertThat(metadata.freeText(), equalTo(freeText));
        assertThat(metadata.createdBy().curatorId(), equalTo("HPO:walterwhite"));
        assertThat(metadata.modifiedBy(), is(empty()));

        // Test variants
        List<CuratedVariant> variants = study.variants();
        assertThat(variants, hasSize(5));

        // sequence
        assertThat(variants.get(0), equalTo(CuratedVariant.of("GRCh37.p13",
                GenomicVariant.of(GRCH37.contigByName("9"),
                        "GRCH_37-9-123,737,057-123,737,057-C-G",
                        Strand.POSITIVE,
                        CoordinateSystem.oneBased(),
                        123_737_057,
                        "C",
                        "G"),
                MendelianVariantMetadata.of("TTCATTTAC[C/G]TCTACTGGC",
                        "promoter",
                        "promoter|reduced-transcription",
                        false,
                        false,
                        "TEST_REGULATOR",
                        "no",
                        "RES_ACT",
                        true,
                        "TF_SYMBOL_TEST",
                        "TF_GENEID_TEST",
                        "down",
                        "in vitro mRNA expression assay"))));
        // symbolic
        assertThat(variants.get(3), equalTo(CuratedVariant.of("GRCh37.p13",
                GenomicVariant.of(GRCH37.contigByName("5"),
                        "GRCH_37-5-149,741,531-149,744,897-N-<DEL>",
                        Strand.POSITIVE,
                        Coordinates.of(CoordinateSystem.oneBased(),
                                149_741_531,
                                ConfidenceInterval.of(-5, 5),
                                149_744_897,
                                ConfidenceInterval.of(-15, 10)),
                        "N",
                        "<DEL>",
                        -3367),
                StructuralVariantMetadata.of("", "structural", "", false, false))));

        // breakend
        assertThat(variants.get(4), equalTo(CuratedVariant.of("GRCh37.p13",
                GenomicVariant.of("GRCH_37-9-133,359,000-13-32,300,000-G-",
                        GenomicBreakend.of(GRCH37.contigByName("9"),
                                "GRCH_37-9-133,359,000-[+]",
                                Strand.POSITIVE,
                                Coordinates.of(CoordinateSystem.zeroBased(),
                                        133_359_000,
                                        ConfidenceInterval.of(-5, 10),
                                        133_359_000,
                                        ConfidenceInterval.of(-5, 10))),
                        GenomicBreakend.of(GRCH37.contigByName("13"),
                                "GRCH_37-13-32,300,000-[+]",
                                Strand.POSITIVE,
                                Coordinates.of(CoordinateSystem.zeroBased(),
                                        32_300_000,
                                        ConfidenceInterval.of(-15, 20),
                                        32_300_000,
                                        ConfidenceInterval.of(-15, 20))),
                        "G", ""),
                StructuralVariantMetadata.of("", "structural", "", false, false))));

        // Test members
        assertThat(study, instanceOf(FamilyStudy.class));
        FamilyStudy familyStudy = (FamilyStudy) study;
        List<? extends PedigreeMember> members = familyStudy.members().toList();
        assertThat(members, hasSize(1));
        assertThat(members.get(0), equalTo(PedigreeMember.of("FAM:001", "", "",
                true,
                List.of(
                        PhenotypicFeature.of(TermId.of("HP:1234567"), false, AgeRange.sinceBirthUntilAge(Period.parse("P10Y5M4D"))),
                        PhenotypicFeature.of(TermId.of("HP:9876543"), true, AgeRange.sinceBirthUntilAge(Period.parse("P10Y5M4D")))
                ),
                List.of(DiseaseStatus.of(DiseaseIdentifier.of(TermId.of("OMIM:219700"), "CYSTIC FIBROSIS; CF"), false)),
                Map.of(
                        "c9dda67d707ab3c69142d891d6a0a4e1", Genotype.HOMOZYGOUS_ALTERNATE, // sequence, mendelian variant
                        "f2e88a99810ce259880f744fbbddc0f3", Genotype.HETEROZYGOUS, // sequence, somatic variant
                        "bfed08fc27778a1587dcaebc2b455718", Genotype.HOMOZYGOUS_ALTERNATE, // sequence, splicing variant
                        "94f38002744f2dfdbad129153880603f", Genotype.HETEROZYGOUS, // symbolic deletion
                        "52665ac160d15a5b235e470935d8b1ab", Genotype.HETEROZYGOUS), // breakend variant
                Period.parse("P10Y5M4D"),
                Sex.MALE)));
    }
}