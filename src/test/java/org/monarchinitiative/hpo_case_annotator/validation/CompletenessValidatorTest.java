package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Optional<DiseaseCaseModel> modelOptional = XMLModelParser.loadDiseaseCaseModel(
                new File("target/test-classes/models/xml/Hull-1994-CFTR.xml"));
        assertTrue(modelOptional.isPresent());
        DiseaseCaseModel model = modelOptional.get();

        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, validator.getErrorMessage());
    }


    /**
     * Test validation of correct real-life model.
     */
    @Test
    public void validateDiseaseCaseArs() throws Exception {
        ValidationResult result = validator.validateDiseaseCase(getArs());
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, validator.getErrorMessage());
    }


    /**
     * Test failure when genome build is missing.
     */
    @Test
    public void testMissingGenomeBuild() throws Exception {
        DiseaseCaseModel model = getArs();
        model.setGenomeBuild(null);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("Missing genome build", validator.getErrorMessage());
    }


    /**
     * Test failure when publication is missing.
     */
    @Test
    public void testMissingPublication() throws Exception {
        DiseaseCaseModel model = getArs();
        model.setPublication(null);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("Missing publication data", validator.getErrorMessage());
    }


    /**
     * Test failure when variant is missing.
     */
    @Test
    public void testMissingVariant() throws Exception {
        DiseaseCaseModel model = getArs();
        model.getVariants().clear();
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("Model must contain at least one variant", validator.getErrorMessage());
    }


    /**
     * Test failure when variant is incomplete.
     */
    @Test
    public void testIncompleteVariant() throws Exception {
        DiseaseCaseModel model = getArs();
        Variant variant = model.getVariants().get(0);

        variant.setChromosome("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals(":10490A>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setChromosome("7");

        variant.setPosition("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:A>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setPosition("10490");

        variant.setReferenceAllele("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:10490>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setReferenceAllele("A");

        variant.setAlternateAllele("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:10490A> - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setAlternateAllele("CC");

        variant.setSnippet("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setSnippet("TATCTT[A/CC]AGGCTTTT");

        variant.setGenotype("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
        variant.setGenotype("heterozygous");

        variant.setVariantClass("");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", validator.getErrorMessage());
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
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("CSS fields are incomplete", validator.getErrorMessage());
        variant.setCrypticPosition("");

        variant.setCrypticSpliceSiteSnippet("ACGT|ACGT");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("CSS fields are incomplete", validator.getErrorMessage());
        variant.setCrypticSpliceSiteSnippet("");

        variant.setCrypticSpliceSiteType("5' cryptic splice site");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(model));
        assertEquals("CSS fields are incomplete", validator.getErrorMessage());
        variant.setCrypticSpliceSiteType("");
    }


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private DiseaseCaseModel getArs() throws Exception {
        File ARS = new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml");
        Optional<DiseaseCaseModel> modelOptional = XMLModelParser.loadDiseaseCaseModel(ARS);
        if (modelOptional.isPresent())
            return modelOptional.get();
        throw new FileNotFoundException(String.format("Couldn't find model file for testing: %s", ARS.getAbsolutePath()));
    }

}