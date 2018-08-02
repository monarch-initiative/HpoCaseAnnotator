package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * At first two real-life model files are tested and then invalid cases are tested. Created by Daniel Danis on 5/25/17.
 */
public class GenomicPositionValidatorTest {

    /**
     * Tested instance.
     */
    private GenomicPositionValidator validator;


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private static DiseaseCaseModel getArs() throws Exception {
        File ARS = new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml");
        Optional<DiseaseCaseModel> modelOptional = XMLModelParser.loadDiseaseCaseModel(ARS);
        if (modelOptional.isPresent())
            return modelOptional.get();
        throw new FileNotFoundException(String.format("Couldn't find model file for testing: %s", ARS.getAbsolutePath()));
    }


    @Before
    public void setUp() throws Exception {
        validator = new GenomicPositionValidator(new File("target/test-classes/testgenome"));
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
     * Test validation of incorrect REF allele.
     */
    @Test
    public void testInvalidAllele() throws Exception {
        DiseaseCaseModel ARS = getArs();
        ARS.getVariants().get(0).setReferenceAllele("GG");
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("genomic reference=\"AA\" at chr7:10490-10491; entered ref=GG/alt=CC/len=2", validator.getErrorMessage());
    }


    /**
     * Test correct snippet validation and error reporting.
     */
    @Test
    public void testSnippetValidation() throws Exception {
        DiseaseCaseModel ARS = getArs();

        String correctSnippet = "TATCTT[A/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(correctSnippet);
        assertEquals(ValidationResult.PASSED, validator.validateDiseaseCase(ARS));
        assertEquals(validator.OKAY, validator.getErrorMessage());

        String missingSnippet = "";
        ARS.getVariants().get(0).setSnippet(missingSnippet);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Snippet wasn't entered!", validator.getErrorMessage());

        String missingLeftSeq = "[A/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingLeftSeq);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find upstream sequence in snippet: [A/CC]AGGCTTTT",
                validator.getErrorMessage());

        String missingLeftBracket = "TA/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingLeftBracket);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find opening bracket in snippet: TA/CC]AGGCTTTT",
                validator.getErrorMessage());

        String missingRightBracket = "TATCTT[A/CCAGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingRightBracket);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find closing bracket in snippet: TATCTT[A/CCAGGCTTTT",
                validator.getErrorMessage());

        String missingRightSeq = "TATCTT[A/CC]";
        ARS.getVariants().get(0).setSnippet(missingRightSeq);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find downstream sequence in snippet: TATCTT[A/CC]",
                validator.getErrorMessage());

        String missingSlash = "TATCTT[ACC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingSlash);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find proper slash (/) in snippet: TATCTT[ACC]AGGCTTTT", validator.getErrorMessage());

        String missingRef = "TATCTT[/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingRef);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for REF in snippet: TATCTT[/CC]AGGCTTTT",
                validator.getErrorMessage());

        String missingAlt = "TATCTT[A/]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingAlt);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for ALT in snippet: TATCTT[A/]AGGCTTTT", validator.getErrorMessage());

        String incorrectRef = "TATCTT[G/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(incorrectRef);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Reference sequence (A) not given correctly in snippet (G): TATCTT[G/CC]AGGCTTTT", validator.getErrorMessage());
    }


    /**
     * Test correct validation of CSS snippet & position.
     */
    @Test
    public void testCrypticSpliceSiteValidation() throws Exception {
        DiseaseCaseModel ARS = getArs(); // here, no CSS info is set by default, validation checked in other tests
        SplicingVariant variant = (SplicingVariant) ARS.getVariants().get(0);
        ARS.getVariants().clear();

        variant.setCrypticPosition("10499");
        variant.setCrypticSpliceSiteSnippet("GGCTTTTG|AGAAAAAAC");
        variant.setCrypticSpliceSiteType("OU_YEAH");
        ARS.getVariants().add(variant);
        assertEquals(ValidationResult.PASSED, validator.validateDiseaseCase(ARS));
        assertEquals(validator.OKAY, validator.getErrorMessage());

        variant.setCrypticPosition("10500");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTG, Genomic sequence: GCTTTTGA",
                validator.getErrorMessage());

        variant.setCrypticPosition("10499");
        variant.setCrypticSpliceSiteSnippet("GGCTTTTGAGAAAAAAC");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Unable to find '|' symbol in CSS snippet GGCTTTTGAGAAAAAAC",
                validator.getErrorMessage());

        variant.setCrypticSpliceSiteSnippet("GGCTTTTG|AGAAAAAAC");
        variant.setCrypticPosition("10499 ");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("CSS position '10499 ' is not valid integer", validator.getErrorMessage());

        variant.setCrypticPosition("10499");
        variant.setCrypticSpliceSiteSnippet("GGCTTTTGA|GAAAAAAC");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTGA, Genomic sequence: AGGCTTTTG",
                validator.getErrorMessage());

        variant.setCrypticSpliceSiteSnippet("GGCTTTT|GAGAAAAAAC");
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTT, Genomic sequence: GCTTTTG",
                validator.getErrorMessage());
    }


    /**
     * Test the variants chr7:g.10490AA>CC, chr7:g.10490AA>CCG
     *
     * @throws Exception
     */
    @Test
    public void testDelInsVariant() throws Exception {
        DiseaseCaseModel ARS = getArs();
        SplicingVariant variant = (SplicingVariant) ARS.getVariants().get(0);
        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("CC");
        variant.setSnippet("TTCTATCTT[AA/CC]GGCTTT");
        assertTrue(validator.variantValid(variant)); // chr7:g.10490AA>CC

        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("CCG");
        variant.setSnippet("TTCTATCTT[AA/CCG]GGCTTT");
        assertTrue(validator.variantValid(variant)); // chr7:g.10490AA>CCG

        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("C");
        variant.setSnippet("TTCTATCTT[AA/C]GGCTTT");
        assertTrue(validator.variantValid(variant)); // chr7:g.10490AA>C
    }
}