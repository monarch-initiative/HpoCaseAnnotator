package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.TestResources;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.refgenome.SingleFastaSequenceDao;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * At first two real-life model files are tested and then invalid cases are tested.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class GenomicPositionValidatorTest {

    private static SequenceDao sequenceDao;

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
        try (InputStream is = GenomicPositionValidatorTest.class.getResourceAsStream("/models/xml/Ars-2000-NF1-95-89.xml")) {
            return XMLModelParser.loadDiseaseCaseModel(is);
        }
    }


    private static DiseaseCaseModel getHull() throws Exception {
        try (InputStream is = GenomicPositionValidatorTest.class.getResourceAsStream("/models/xml/Hull-1994-CFTR.xml")) {
            return XMLModelParser.loadDiseaseCaseModel(is);
        }
    }


    @BeforeClass
    public static void setUpBefore() throws Exception {
        sequenceDao = new SingleFastaSequenceDao(TestResources.TEST_REF_GENOME_FASTA);
    }


    @AfterClass
    public static void tearDownAfter() throws Exception {
        sequenceDao.close();
    }


    @Before
    public void setUp() throws Exception {
        validator = new GenomicPositionValidator(sequenceDao);
    }


    /**
     * Test validation of correct real-life model.
     */
    @Test
    public void validateDiseaseCaseHull() throws Exception {
        ValidationResult result = validator.validateDiseaseCase(getHull());
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, result.getMessage());
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
     * Test validation of incorrect REF allele.
     */
    @Test
    public void testInvalidAllele() throws Exception {
        DiseaseCaseModel ARS = getArs();
        ARS.getVariants().get(0).setReferenceAllele("GG");
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("genomic reference=\"AA\" at chr7:10490-10491; entered ref=GG/alt=CC/len=2", result.getMessage());
    }


    /**
     * Test correct snippet validation and error reporting.
     */
    @Test
    public void testSnippetValidation() throws Exception {
        DiseaseCaseModel ARS = getArs();

        String correctSnippet = "TATCTT[A/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(correctSnippet);
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, result.getMessage());

        String missingSnippet = "";
        ARS.getVariants().get(0).setSnippet(missingSnippet);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Snippet wasn't entered!", result.getMessage());

        String missingLeftSeq = "[A/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingLeftSeq);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find upstream sequence in snippet: [A/CC]AGGCTTTT",
                result.getMessage());

        String missingLeftBracket = "TA/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingLeftBracket);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find opening bracket in snippet: TA/CC]AGGCTTTT",
                result.getMessage());

        String missingRightBracket = "TATCTT[A/CCAGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingRightBracket);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find closing bracket in snippet: TATCTT[A/CCAGGCTTTT",
                result.getMessage());

        String missingRightSeq = "TATCTT[A/CC]";
        ARS.getVariants().get(0).setSnippet(missingRightSeq);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find downstream sequence in snippet: TATCTT[A/CC]",
                result.getMessage());

        String missingSlash = "TATCTT[ACC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingSlash);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find proper slash (/) in snippet: TATCTT[ACC]AGGCTTTT", result.getMessage());

        String missingRef = "TATCTT[/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingRef);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for REF in snippet: TATCTT[/CC]AGGCTTTT",
                result.getMessage());

        String missingAlt = "TATCTT[A/]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(missingAlt);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for ALT in snippet: TATCTT[A/]AGGCTTTT", result.getMessage());

        String incorrectRef = "TATCTT[G/CC]AGGCTTTT";
        ARS.getVariants().get(0).setSnippet(incorrectRef);
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Reference sequence (A) not given correctly in snippet (G): TATCTT[G/CC]AGGCTTTT", result.getMessage());
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
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.PASSED, validator.validateDiseaseCase(ARS));
        assertEquals(validator.OKAY, result.getMessage());

        variant.setCrypticPosition("10500");
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTG, Genomic sequence: GCTTTTGA",
                result.getMessage());

        variant.setCrypticPosition("10499");
        variant.setCrypticSpliceSiteSnippet("GGCTTTTGAGAAAAAAC");
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Unable to find '|' symbol in CSS snippet GGCTTTTGAGAAAAAAC",
                result.getMessage());

        variant.setCrypticSpliceSiteSnippet("GGCTTTTG|AGAAAAAAC");
        variant.setCrypticPosition("10499 ");
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("CSS position '10499 ' is not valid integer", result.getMessage());

        variant.setCrypticPosition("10499");
        variant.setCrypticSpliceSiteSnippet("GGCTTTTGA|GAAAAAAC");
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTGA, Genomic sequence: AGGCTTTTG",
                result.getMessage());

        variant.setCrypticSpliceSiteSnippet("GGCTTTT|GAGAAAAAAC");
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTT, Genomic sequence: GCTTTTG",
                result.getMessage());
    }


    /**
     * Test the variants chr7:g.10490AA>CC, chr7:g.10490AA>CCG
     *
     * @throws Exception blah
     */
    @Test
    public void testDelInsVariant() throws Exception {
        DiseaseCaseModel ARS = getArs();
        SplicingVariant variant = (SplicingVariant) ARS.getVariants().get(0);
        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("CC");
        variant.setSnippet("TTCTATCTT[AA/CC]GGCTTT");
        assertEquals(ValidationResult.PASSED, validator.variantValid(variant)); // chr7:g.10490AA>CC

        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("CCG");
        variant.setSnippet("TTCTATCTT[AA/CCG]GGCTTT");
        assertEquals(ValidationResult.PASSED, validator.variantValid(variant)); // chr7:g.10490AA>CCG

        variant.setReferenceAllele("AA");
        variant.setAlternateAllele("C");
        variant.setSnippet("TTCTATCTT[AA/C]GGCTTT");
        assertEquals(ValidationResult.PASSED, validator.variantValid(variant)); // chr7:g.10490AA>C
    }
}