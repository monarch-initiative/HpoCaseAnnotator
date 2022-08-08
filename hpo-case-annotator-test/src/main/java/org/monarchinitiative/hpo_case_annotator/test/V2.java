package org.monarchinitiative.hpo_case_annotator.test;

import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

class V2 {

    private static final GenomicAssembly HG19 = GenomicAssemblies.GRCh37p13();

    public static FamilyStudy comprehensiveFamilyStudy() {
        return FamilyStudy.of("familyStudyTest", getPublication(), getVariants(), getPedigree(), getStudyMetadata());
    }

    public static CohortStudy comprehensiveCohortStudy() {
        Collection<? extends Individual> members = getCohortMembers();

        return CohortStudy.of("cohortStudyTest", getPublication(), getVariants(), members, getStudyMetadata());
    }

    private static Publication getPublication() {
        return Publication.of(
                String.join(", ", List.of("Beygó J", "Buiting K", "Seland S", "Lüdecke HJ", "Hehr U", "Lich C", "Prager B", "Lohmann DR", "Wieczorek D")),
                "First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome",
                "Mol Syndromol",
                2012,
                "2(2)",
                "53-59",
                "22712005");
    }

    private static List<CuratedVariant> getVariants() {
        return List.of(mendelianVariant(), somaticVariant(), splicingVariant(), symbolicDeletion(), symbolicBreakendVariant());
    }

    private static CuratedVariant mendelianVariant() {
        VariantMetadata metadata = MendelianVariantMetadata.of(
                "TTCATTTAC[C/G]TCTACTGG",
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
                "in vitro mRNA expression assay");
        GenomicVariant variant = GenomicVariant.of(HG19.contigByName("9"),
                "mendelian",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 123_737_056, 123_737_057),
                "C",
                "G");
        return CuratedVariant.of(HG19.name(), variant, metadata);
    }

    private static CuratedVariant somaticVariant() {
        VariantMetadata metadata = SomaticVariantMetadata.of(
                "TTCATTTAC[C/A]TCTACTGG",
                "5UTR",
                "5UTR|transcription",
                false,
                false,
                "SOMATIC_REGULATOR",
                "up",
                "SOMATIC_RP_PERC",
                "TF_GENE_SYMBOL_TEST",
                "up",
                78,
                100
        );
        GenomicVariant variant = GenomicVariant.of(HG19.contigByName("9"),
                "somatic",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 123_737_056, 123_737_057),
                "C",
                "A");
        return CuratedVariant.of(HG19.name(), variant, metadata);
    }

    private static CuratedVariant splicingVariant() {
        VariantMetadata metadata = SplicingVariantMetadata.of(
                "TTCATTTAC[C/T]TCTACTGG",
                "splicing",
                "splicing|3css|activated",
                true,
                false,
                123737090,
                SplicingVariantMetadata.CrypticSpliceSiteType.FIVE_PRIME,
                "ATATG|GCGAGTTCTT",
                "Cryptic splicing",
                true,
                false,
                true,
                false,
                true,
                false,
                true,
                false,
                true);
        GenomicVariant variant = GenomicVariant.of(HG19.contigByName("9"),
                "splicing",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 123_737_056, 123_737_057),
                "C",
                "T");
        return CuratedVariant.of(HG19.name(), variant, metadata);
    }

    private static CuratedVariant symbolicDeletion() {
        VariantMetadata metadata = StructuralVariantMetadata.of(
                "",
                "structural",
                "intronic deletion",
                true,
                false);
        GenomicVariant variant = GenomicVariant.of(HG19.contigByName("5"),
                "symbolic_DEL",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 149_741_530, ConfidenceInterval.of(-5, 10), 149_744_897, ConfidenceInterval.of(-10, 20)),
                "N",
                "<DEL>",
                -(149_744_897 - 149_741_531 + 1));
        return CuratedVariant.of(HG19.name(), variant, metadata);
    }

    private static CuratedVariant symbolicBreakendVariant() {
        GenomicBreakend left = GenomicBreakend.of(HG19.contigByName("9"),
                "left",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 133_359_000, ConfidenceInterval.of(-5, 15), 133_359_000, ConfidenceInterval.precise()));
        GenomicBreakend right = GenomicBreakend.of(HG19.contigByName("13"),
                "right",
                Strand.POSITIVE,
                Coordinates.of(CoordinateSystem.zeroBased(), 32_300_000, 32_300_000));
        VariantMetadata metadata = StructuralVariantMetadata.of(
                "",
                "structural",
                "intronic deletion",
                true,
                false);
        GenomicVariant variant = GenomicVariant.of("symbolic_breakend", left, right, "G", "ACGT");
        return CuratedVariant.of(HG19.name(), variant, metadata);
    }

    private static Pedigree getPedigree() {
        DiseaseIdentifier diseaseIdentifier = DiseaseIdentifier.of(TermId.of("OMIM:219700"), "CYSTIC FIBROSIS; CF");
        List<DiseaseStatus> diseases = List.of(DiseaseStatus.of(diseaseIdentifier, false));

        List<VariantGenotype> genotypes = List.of(
                VariantGenotype.of(mendelianVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE),
                VariantGenotype.of(somaticVariant().md5Hex(), Genotype.HETEROZYGOUS),
                VariantGenotype.of(splicingVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE),
                VariantGenotype.of(symbolicDeletion().md5Hex(), Genotype.HETEROZYGOUS),
                VariantGenotype.of(symbolicBreakendVariant().md5Hex(), Genotype.HETEROZYGOUS));

        TimeElement hypertensionOnset = TimeElement.of(TimeElement.TimeElementCase.AGE, null, Age.of(4, 6, null), null, null);
        TimeElement bronchiectasisOnset = TimeElement.of(TimeElement.TimeElementCase.AGE_RANGE, null, null, AgeRange.of(Age.of(10, null, null), Age.of(10, 6, null)), null);
        List<PhenotypicFeature> phenotypes = List.of(
                PhenotypicFeature.of(TermId.of("HP:0000822"), "Hypertension", true, hypertensionOnset, null), // Hypertension
                PhenotypicFeature.of(TermId.of("HP:0002110"), "Bronchiectasis", false, bronchiectasisOnset, null) // Bronchiectasis
        );

        TimeElement age = TimeElement.of(TimeElement.TimeElementCase.AGE, null, Age.of(10, 5, 4), null, null);
        VitalStatus vitalStatus = VitalStatus.of(VitalStatus.Status.ALIVE, null);
        return Pedigree.of(
                List.of(PedigreeMember.of("FAM:001",
                        "FAM:002",
                        null,
                        true, phenotypes, diseases, genotypes, age,
                        vitalStatus,
                        Sex.MALE))
        );
    }

    private static Collection<? extends Individual> getCohortMembers() {
        DiseaseIdentifier diseaseIdentifier = DiseaseIdentifier.of(TermId.of("OMIM:219700"), "CYSTIC FIBROSIS; CF");
        // abc
        // The phenotypic features are nonsense, just for testing.
        TimeElement abcHypertensionOnset = TimeElement.of(TimeElement.TimeElementCase.GESTATIONAL_AGE, GestationalAge.of(38, 3), null, null, null);
        TimeElement abcBronchiectasisOnset = TimeElement.of(TimeElement.TimeElementCase.AGE, null, Age.of(10, 0, 20), null, null);
        TimeElement abcAge = TimeElement.of(TimeElement.TimeElementCase.AGE, null, Age.of(14, null, null), null, null);
        VitalStatus abcVitalStatus = VitalStatus.of(VitalStatus.Status.ALIVE, null);
        Individual abc = Individual.of("abc",
                List.of(
                        PhenotypicFeature.of(TermId.of("HP:0000822"), "Hypertension", true, abcHypertensionOnset, null), // Hypertension
                        PhenotypicFeature.of(TermId.of("HP:0002110"), "Bronchiectasis", false, abcBronchiectasisOnset, null) // Bronchiectasis
                ),
                List.of(DiseaseStatus.of(diseaseIdentifier, false)),
                List.of(
                        VariantGenotype.of(mendelianVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE),
                        VariantGenotype.of(somaticVariant().md5Hex(), Genotype.HETEROZYGOUS),
                        VariantGenotype.of(splicingVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE),
                        VariantGenotype.of(symbolicDeletion().md5Hex(), Genotype.HETEROZYGOUS),
                        VariantGenotype.of(symbolicBreakendVariant().md5Hex(), Genotype.HETEROZYGOUS)),
                abcAge,
                abcVitalStatus,
                Sex.MALE);

        // def
        // Onset somewhere in the 16th year of life.
        Age start = Age.of(15, null, null);
        Age end = Age.of(16, null, null);
        TimeElement defHypertensionOnset = TimeElement.of(TimeElement.TimeElementCase.AGE_RANGE, null, null, AgeRange.of(start, end), null);
        TermId adultOnset = TermId.of("HP:0003581");
        TimeElement defBronchiectasisOnset = TimeElement.of(TimeElement.TimeElementCase.ONTOLOGY_CLASS, null, null, null, adultOnset);
        TimeElement defAge = TimeElement.of(TimeElement.TimeElementCase.AGE, null, Age.of(25, null, null), null, null);
        VitalStatus defVitalStatus = VitalStatus.of(VitalStatus.Status.DECEASED, TimeElement.age(Age.of(35, null, null)));
        Individual def = Individual.of("def",
                List.of(
                        PhenotypicFeature.of(TermId.of("HP:0000822"), "Hypertension", false, defHypertensionOnset, null), // Hypertension
                        PhenotypicFeature.of(TermId.of("HP:0002110"), "Bronchiectasis", true, defBronchiectasisOnset, null) // Bronchiectasis
                ),
                List.of(DiseaseStatus.of(diseaseIdentifier, true)),
                List.of(
                        VariantGenotype.of(mendelianVariant().md5Hex(), Genotype.HOMOZYGOUS_REFERENCE),
                        VariantGenotype.of(somaticVariant().md5Hex(), Genotype.HOMOZYGOUS_REFERENCE),
                        VariantGenotype.of(splicingVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE),
                        VariantGenotype.of(symbolicDeletion().md5Hex(), Genotype.HETEROZYGOUS),
                        VariantGenotype.of(symbolicBreakendVariant().md5Hex(), Genotype.HOMOZYGOUS_ALTERNATE)),
                defAge,
                defVitalStatus,
                Sex.FEMALE);

        return List.of(abc, def);
    }

    private static StudyMetadata getStudyMetadata() {
        String freeText = """
                Authors are describing a mutations in CFTR exon 13 that appears to contain two 3'CSS utilization of which is increased when there is a mutation in ESE element present in exon 13 (Figure 2.).

                The 3'CSS whose coordinates are recorded in variants is the dominant one (Figure 2. D, D248). However, there exists also another (D195) which has coordinates: 117232182, 3 splice site, CAATTTAG|TGCAGAAA .
                """;
        EditHistory editHistory = EditHistory.of("HPO:ddanis", "2.0.0", Instant.parse("2021-01-01T12:00:00.000000000Z"));
        List<EditHistory> modified = List.of(
                EditHistory.of("HPO:ddanis", "2.0.0", Instant.parse("2021-01-02T12:00:00.000000000Z")),
                EditHistory.of("HPO:ddanis", "2.0.0", Instant.parse("2021-01-02T14:00:00.000000000Z"))
        );

        return StudyMetadata.of(freeText, editHistory, modified);
    }


}
