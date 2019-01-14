package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class VariantValidationDataSyntaxValidatorTest {


    private VariantValidationDataSyntaxValidator instance = new VariantValidationDataSyntaxValidator();


    @Test
    public void failOnEmptySplicingValidation() {
        VariantValidation splicingValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SPLICING)
                .build();

        final List<ValidationResult> results = instance.validate(splicingValidation);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("At least one splicing validation type should be checked")));
    }

    @Test
    public void validateSplicingValidation() {
        VariantValidation splicingValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SPLICING)
                .setCDnaSequencingValidation(true)
                .build();

        final List<ValidationResult> results = instance.validate(splicingValidation);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.pass()));
    }

    @Test
    public void validateSomaticValidation() {
        final VariantValidation somaticValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SOMATIC)
                .build();

        final List<ValidationResult> results = instance.validate(somaticValidation);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.pass()));
    }


    @Test
    public void validateMendelianValidation() {
        final VariantValidation mendelianValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.MENDELIAN)
                .build();


        final List<ValidationResult> results = instance.validate(mendelianValidation);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.pass()));
    }


    @Test
    public void failOnEmptyValidation() {
        final VariantValidation defaultInstance = VariantValidation.getDefaultInstance();

        final List<ValidationResult> results = instance.validate(defaultInstance);

        assertThat(results, hasItem(ValidationResult.fail("Variant validation is missing")));
    }
}