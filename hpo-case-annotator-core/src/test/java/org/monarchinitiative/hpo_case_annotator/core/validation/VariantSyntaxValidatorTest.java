package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.core.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class VariantSyntaxValidatorTest {

    private VariantSyntaxValidator validator;


    @Before
    public void setUp() throws Exception {
        validator = new VariantSyntaxValidator();
    }

    @Test
    public void variantWithUnknownGenomeAssembly() {
        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY)
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Unknown genome assembly")));
    }

    @Test
    public void variantWithMissingChromosome() {
        Variant variant = Variant.newBuilder().build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Missing chromosome")));
    }

    @Test
    public void variantWithInvalidContig() {
        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setContig("G")
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Invalid chromosome format: G")));
    }

    @Test
    public void variantWithZeroAtPosition() {
        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setPos(0)
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Position must be positive: 0")));
    }

    @Test
    public void variantWithMissingRefAllele() {
        Variant variant = Variant.newBuilder().build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Missing reference allele")));
    }

    @Test
    public void variantWithInvalidRefAllele() {
        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setRefAllele("X")
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Invalid reference allele format: X")));
    }

    @Test
    public void variantWithMissingAltAllele() {
        Variant variant = Variant.newBuilder().build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Missing alternate allele")));
    }

    @Test
    public void variantWithInvalidAltAllele() {
        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setAltAllele("X")
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Invalid alternate allele format: X")));
    }

    @Test
    public void variantWithUndefinedGenotype() {
        Variant variant = Variant.newBuilder().build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Undefined genotype")));
    }

    @Test
    public void variantWithMissingSnippet() {
        Variant variant = Variant.newBuilder().build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Missing snippet")));
    }

    @Test
    public void variantWithInvalidSnippet() {
        Variant variant = Variant.newBuilder()
                .setSnippet("INVALID[X/Z]SNIPPET")
                .build();

        final List<ValidationResult> results = validator.validate(variant);

        assertThat(results, hasItem(ValidationResult.fail("Invalid snippet format: INVALID[X/Z]SNIPPET")));
    }

    @Test
    public void structuralVariant() {
        Variant variant = DiseaseCaseModelExample.exampleStructuralVariant();

        List<ValidationResult> results = validator.validate(variant);

        assertThat(results, is(empty()));
    }
}