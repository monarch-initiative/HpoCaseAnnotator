package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.core.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;


public class CompletenessValidatorTest {

    /**
     * Tested instance.
     */
    private CompletenessValidator validator;


    @Before
    public void setUp() throws Exception {
        validator = new CompletenessValidator();
    }


    @Test
    public void validateCaseWithMissingPublication() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder().build();

        final List<ValidationResult> results = validator.validate(diseaseCase);

        assertThat(results, hasItem(ValidationResult.fail("Publication data is not set")));
    }

    @Test
    public void validateCaseWithMissingGene() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder().build();

        final List<ValidationResult> results = validator.validate(diseaseCase);

        assertThat(results, hasItem(ValidationResult.fail("Gene data is not complete")));
    }

    @Test
    public void validateCaseWithGeneWithNegativeEntrezId() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder()
                .setGene(Gene.newBuilder()
                        .setEntrezId(-5)
                        .setSymbol("HNF4A")
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(diseaseCase);

        assertThat(results, hasItem(ValidationResult.fail("Entrez ID is smaller than 0")));
    }

    @Test
    public void validateCaseWithNoVariant() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder().build();

        final List<ValidationResult> results = validator.validate(diseaseCase);

        assertThat(results, hasItem(ValidationResult.fail("At least one variant should be present")));
    }

    @Test
    public void validateCaseWithVariant() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder()
                .addVariant(DiseaseCaseModelExample.makeVariantWithSplicingValidation())
                .build();

        final List<ValidationResult> results = validator.validate(diseaseCase);

        assertThat(results, not(hasItem(ValidationResult.fail("At least one variant should be present"))));
    }

    @Test
    public void validateCaseWithNoVariantAndNoVariantValidator() {
        DiseaseCase diseaseCase = DiseaseCase.newBuilder().build();

        CompletenessValidator anotherValidator = new CompletenessValidator(null);
        final List<ValidationResult> results = anotherValidator.validate(diseaseCase);

        assertThat(results, hasItem(ValidationResult.fail("At least one variant should be present")));
    }
}


//    /**
//     * Test validation of correct real-life model.
//     */
//    @Test
//    public void validateDiseaseCaseHull() throws Exception {
//        try (InputStream io = new FileInputStream(new File("target/test-classes/models/xml/Hull-1994-CFTR.xml"))) {
//            DiseaseCase model = XMLModelParser.loadDiseaseCase(io)
//                    .orElseThrow(() -> new Exception("Unable to read test data from target/test-classes/models/xml/Hull-1994-CFTR.xml"));
//            final List<ValidationResult> validationResults = validator.validate(model);
////            validationResults.forEach(System.out::println);
////            ValidationResult result = validator.validateDiseaseCase(model);
////            assertEquals(ValidationResult.PASSED, result);
////            assertEquals(validator.OKAY, result.getMessage());
//        }
//    }
//
//
//    /**
//     * Test validation of correct real-life model.
//     */
//    @Test
//    public void validateDiseaseCaseArs() throws Exception {
////        ValidationResult result = validator.validateDiseaseCase(getArs());
////        assertEquals(ValidationResult.PASSED, result);
////        assertEquals(validator.OKAY, result.getMessage());
//    }
//
//
//    /**
//     * Test failure when genome forAllValidations is missing.
//     */
//    @Test
//    public void testMissingGenomeBuild() throws Exception {
//        DiseaseCase model = getArs();
//
////        ValidationResult result = validator.validateDiseaseCase(model.toBuilder().setGenomeBuild(null).forAllValidations());
////        assertEquals(ValidationResult.FAILED, result);
////        assertEquals("Missing genome forAllValidations", result.getMessage());
//    }
//
//
//    /**
//     * Test failure when publication is missing.
//     */
//    @Test
//    public void testMissingPublication() throws Exception {
//        DiseaseCase model = getArs();
////        ValidationResult result = validator.validateDiseaseCase(model.toBuilder().setPublication(Publication.getDefaultInstance()).forAllValidations());
////        assertEquals(ValidationResult.FAILED, result);
////        assertEquals("Missing publication data", result.getMessage());
//    }
//
//
//    /**
//     * Test failure when variant is missing.
//     */
//    @Test
//    public void testMissingVariant() throws Exception {
//        DiseaseCase model = getArs().toBuilder().clearVariant().build();
////        ValidationResult result = validator.validateDiseaseCase(model);
////        assertEquals(ValidationResult.FAILED, result);
////        assertEquals("Model must contain at least one variant", result.getMessage());
//    }
//
//
//    /**
//     * Test failure when variant is incomplete.
//     */
//    @Test
//    public void testIncompleteVariant() throws Exception {
//        DiseaseCase model = getArs();
//        Variant variant = model.getVariant(0);
///*
//
//        ValidationResult result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setContig("").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals(":10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setPos(0).forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setRefAllele("").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:10490>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setAltAllele("").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:10490A> - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setSnippet("").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setGenotype(Genotype.UNDEFINED).forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
//
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setVariantClass("").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());*/
//    }
//
//
//    /**
//     * Test failure when attributes specific to splicing variant are incomplete.
//     */
//    @Test
//    public void testIncompleteSplicingVariant() throws Exception {
//        DiseaseCase model = getArs();
//        Variant variant = model.getVariant(0);
//
////        ValidationResult result = validator.validateDiseaseCase(model.toBuilder()
////                .setVariant(0, variant.toBuilder().setCrypticPosition(12345).build())
////                .build());
//        /*assertEquals(ValidationResult.FAILED, result);
//        assertEquals("CSS fields are incomplete", result.getMessage());
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setCrypticSpliceSiteSnippet("ACGT|ACGT").forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("CSS fields are incomplete", result.getMessage());
//
//        result = validator.validateDiseaseCase(model.toBuilder()
//                .setVariant(0, variant.toBuilder().setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME).forAllValidations())
//                .forAllValidations());
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("CSS fields are incomplete", result.getMessage());*/
//    }
