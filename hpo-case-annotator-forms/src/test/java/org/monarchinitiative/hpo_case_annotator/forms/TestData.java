package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.forms.study.GenotypedVariant;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescriptionSimple;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadataContext;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;

import java.time.Period;
import java.util.List;
import java.util.Map;

public class TestData {

    private static final GenomicAssembly GRCH38 = GenomicAssemblies.GRCh38p13();
    private static final Contig CHR_X = GRCH38.contigByName("chrX");

    public static List<GenotypedVariant> makeTestVariants() {
        return List.of(
                GenotypedVariant.of(CuratedVariant.of(GRCH38.name(), Variant.of(CHR_X, "abc", Strand.POSITIVE, Coordinates.of(CoordinateSystem.oneBased(), 100_000, 100_000), "A", "C"), variantMetadata()), Genotype.UNKNOWN),
                GenotypedVariant.of(CuratedVariant.of(GRCH38.name(), Variant.of(CHR_X, "def", Strand.POSITIVE, Coordinates.of(CoordinateSystem.oneBased(), 20, 20), "G", "T"), variantMetadata()), Genotype.UNKNOWN),
                GenotypedVariant.of(CuratedVariant.of(GRCH38.name(), Variant.of(CHR_X, "ghi", Strand.POSITIVE, Coordinates.of(CoordinateSystem.oneBased(), 30, 30), "T", "TCA"), variantMetadata()), Genotype.UNKNOWN)
        );
    }

    public static Individual makeIndividual() {
        return Individual.of("ABC",
                phenotypicFeatures(), diseases(), Map.of(), Period.of(15, 0, 0),
                Sex.MALE);
    }

    public static List<DiseaseStatus> diseases() {
        return List.of(
                DiseaseStatus.of(TermId.of("OMIM:154700"), "Marfan Syndrome", false),
                DiseaseStatus.of(TermId.of("OMIM:145500"), "Hypertension, Essential", true)
        );
    }

    public static List<PhenotypicFeature> phenotypicFeatures() {
        return List.of(
                PhenotypicFeature.of(TermId.of("HP:0100807"), false, AgeRange.sinceBirthUntilAge(Period.of(15, 0, 0))), // Long fingers
                PhenotypicFeature.of(TermId.of("HP:0001166"), true, AgeRange.sinceBirthUntilAge(Period.of(15, 0, 0))), // Arachnodactyly
                PhenotypicFeature.of(TermId.of("HP:0000822"), false, AgeRange.of(Period.of(12, 0, 0), Period.of(15, 0, 0)))
        );
    }

    public static List<PhenotypeDescription> sampleValues() {
        return List.of(PhenotypeDescriptionSimple.of(
                        TermId.of("HP:0031972"), "Presyncope", Period.ZERO, Period.of(1, 2, 3), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0001166"), "Arachnodactyly", Period.of(12, 0, 1), Period.of(12, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0001167"), "Abnormality of finger", Period.of(15, 9, 4), Period.of(20, 7, 6), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0000822"), "Hypertension", Period.of(28, 3, 15), Period.of(40, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:0011025"), "Abnormal cardiovascular system physiology", Period.ZERO, Period.of(80, 0, 0), true)
        );
    }

    public static VariantMetadata variantMetadata() {
        return VariantMetadata.of(VariantMetadataContext.UNKNOWN, "ACGT[A/C]TCGT", "coding", "coding|missense", true, true);
    }
}
