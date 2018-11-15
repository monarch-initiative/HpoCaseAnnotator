package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@Ignore // TODO - make these tests work
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
            DiseaseCase model = XMLModelParser.loadDiseaseCase(io);
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
        DiseaseCase model = getArs();

        ValidationResult result = validator.validateDiseaseCase(model.toBuilder().setGenomeBuild(null).build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Missing genome build", result.getMessage());
    }


    /**
     * Test failure when publication is missing.
     */
    @Test
    public void testMissingPublication() throws Exception {
        DiseaseCase model = getArs();
        ValidationResult result = validator.validateDiseaseCase(model.toBuilder().setPublication(Publication.getDefaultInstance()).build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Missing publication data", result.getMessage());
    }


    /**
     * Test failure when variant is missing.
     */
    @Test
    public void testMissingVariant() throws Exception {
        DiseaseCase model = getArs().toBuilder().clearVariant().build();
        ValidationResult result = validator.validateDiseaseCase(model);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("Model must contain at least one variant", result.getMessage());
    }


    /**
     * Test failure when variant is incomplete.
     */
    @Test
    public void testIncompleteVariant() throws Exception {
        DiseaseCase model = getArs();
        Variant variant = model.getVariant(0);


        ValidationResult result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setContig("").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals(":10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());


        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setPos(0).build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());

        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setRefAllele("").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490>CC - SPLICING - At least one mandatory field is empty", result.getMessage());

        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setAltAllele("").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A> - SPLICING - At least one mandatory field is empty", result.getMessage());


        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setSnippet("").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());


        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setGenotype(Genotype.UNDEFINED).build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());


        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setVariantClass("").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("7:10490A>CC - SPLICING - At least one mandatory field is empty", result.getMessage());
    }


    /**
     * Test failure when attributes specific to splicing variant are incomplete.
     */
    @Test
    public void testIncompleteSplicingVariant() throws Exception {
        DiseaseCase model = getArs();
        Variant variant = model.getVariant(0);

        ValidationResult result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setCrypticPosition(12345).build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());

        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setCrypticSpliceSiteSnippet("ACGT|ACGT").build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());

        result = validator.validateDiseaseCase(model.toBuilder()
                .setVariant(0, variant.toBuilder().setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME).build())
                .build());
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("CSS fields are incomplete", result.getMessage());
    }


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCase} instance for testing.
     */
    private DiseaseCase getArs() throws Exception {
        try (InputStream is = new FileInputStream(new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml"))) {
            return XMLModelParser.loadDiseaseCase(is);
        }
    }

}