package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VariantValidationDataSyntaxValidatorTest {


    private VariantValidationDataSyntaxValidator instance = new VariantValidationDataSyntaxValidator();


    @Test
    public void validateEmptySplicingValidation() {
        VariantValidation splicingValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SPLICING)
                .build();

        final List<ValidationResult> results = instance.validate(splicingValidation);

        assertThat(results.size(), is(0));
    }

    @Test
    public void validateSplicingValidation() {
        VariantValidation splicingValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SPLICING)
                .setCDnaSequencingValidation(true)
                .build();

        final List<ValidationResult> results = instance.validate(splicingValidation);

        assertThat(results.size(), is(0));
    }

    @Test
    public void validateSomaticValidation() {
        final VariantValidation somaticValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.SOMATIC)
                .build();

        final List<ValidationResult> results = instance.validate(somaticValidation);

        assertThat(results.size(), is(0));
    }


    @Test
    public void validateMendelianValidation() {
        final VariantValidation mendelianValidation = VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.MENDELIAN)
                .build();


        final List<ValidationResult> results = instance.validate(mendelianValidation);

        assertThat(results.size(), is(0));
    }


    @Test
    public void failOnEmptyValidation() {
        final VariantValidation defaultInstance = VariantValidation.getDefaultInstance();

        final List<ValidationResult> results = instance.validate(defaultInstance);

        assertThat(results, hasItem(ValidationResult.fail("Variant validation is missing")));
    }
}