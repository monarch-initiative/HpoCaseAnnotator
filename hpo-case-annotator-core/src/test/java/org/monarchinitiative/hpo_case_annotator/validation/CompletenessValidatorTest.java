package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class CompletenessValidatorTest {

    /**
     * Tested instance.
     */
    private CompletenessValidator validator;


    @Before
    public void setUp() throws Exception {
        validator = new CompletenessValidator();
    }


    /**
     * Test validation of correct real-life model.
     */
    @Test
    public void validateDiseaseCaseHull() throws Exception {
        try (InputStream io = new FileInputStream(new File("target/test-classes/models/xml/Hull-1994-CFTR.xml"))) {
            DiseaseCaseModel model = XMLModelParser.loadDiseaseCaseModel(io);
            ValidationResult result = validator.validateDiseaseCase(model);
            assertEquals(ValidationResult.PASSED, result);
            assertEquals(validator.OKAY, result.getMessage());
        }
    }


    /**
     * Test validation of correct real-life model.
     */
    @Test
    public void validateDiseaseCaseArs() throws Exception {
        ValidationResult result = validator.validateDiseaseCase(getArs());
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, result.getMessage());
    }


    /**
     * Test failure when genome build is missing.
     */
    @Test
    public void testMissingGenomeBuild() throws Exception {
        DiseaseCaseModel model = getArs();
        model.setGenomeBuild(null);
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Missing genome build", result.getMessage());
    }


    /**
     * Test failure when publication is missing.
     */
    @Test
    public void testMissingPublication() throws Exception {
        DiseaseCaseModel model = getArs();
        model.setPublication(null);
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Missing publication data", result.getMessage());
    }


    /**
     * Test failure when variant is missing.
     */
    @Test
    public void testMissingVariant() throws Exception {
        DiseaseCaseModel model = getArs();
        model.getVariants().clear();
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Model must contain at least one variant", result.getMessage());
    }


    /**
     * Test failure when variant is incomplete.
     */
    @Test
    public void testIncompleteVariant() throws Exception {
        DiseaseCaseModel model = getArs();
        Variant variant = model.getVariants().get(0);

        variant.setChromosome("");
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals(":10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setChromosome("7");

        variant.setPosition("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setPosition("10490");

        variant.setReferenceAllele("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setReferenceAllele("A");

        variant.setAlternateAllele("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A> - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setAlternateAllele("CC");

        variant.setSnippet("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setSnippet("TATCTT[A/CC]AGGCTTTT");

        variant.setGenotype("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setGenotype("heterozygous");

        variant.setVariantClass("");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
        variant.setVariantClass("coding");
    }


    /**
     * Test failure when attributes specific to splicing variant are incomplete.
     */
    @Test
    public void testIncompleteSplicingVariant() throws Exception {
        DiseaseCaseModel model = getArs();
        SplicingVariant variant = (SplicingVariant) model.getVariants().get(0);

        variant.setCrypticPosition("12345");
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());
        variant.setCrypticPosition("");

        variant.setCrypticSpliceSiteSnippet("ACGT|ACGT");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());
        variant.setCrypticSpliceSiteSnippet("");

        variant.setCrypticSpliceSiteType("5' cryptic splice site");
        result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());
        variant.setCrypticSpliceSiteType("");
    }


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private DiseaseCaseModel getArs() throws Exception {
        try (InputStream is = new FileInputStream(new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml"))) {
            return XMLModelParser.loadDiseaseCaseModel(is);
        }
    }

}