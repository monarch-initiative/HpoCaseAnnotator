package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.*;
import org.monarchinitiative.hpo_case_annotator.core.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.CrypticSpliceSiteType;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SingleFastaSequenceDao;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * At first two real-life model files are tested and then invalid cases are tested.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
@Ignore // TODO - make these tests work
public class GenomicPositionValidatorTest {

    private static SequenceDao sequenceDao;

    /**
     * Tested instance.
     */
    private GenomicPositionValidator validator;


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCase} instance for testing.
     */
    private static DiseaseCase getArs() throws Exception {
        try (InputStream is = GenomicPositionValidatorTest.class.getResourceAsStream("/models/xml/Ars-2000-NF1-95-89.xml")) {
            return XMLModelParser.loadDiseaseCase(is);
        }
    }


    private static DiseaseCase getHull() throws Exception {
        try (InputStream is = GenomicPositionValidatorTest.class.getResourceAsStream("/models/xml/Hull-1994-CFTR.xml")) {
            return XMLModelParser.loadDiseaseCase(is);
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
        DiseaseCase ARS = getArs();

        ARS = ARS.toBuilder().setVariant(0, Variant.newBuilder(ARS.getVariant(0)).setRefAllele("GG").build()).build();
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, result);
        assertEquals("genomic reference=\"AA\" at chr7:10490-10491; entered ref=GG/alt=CC/len=2", result.getMessage());
    }


    /**
     * Test correct snippet validation and error reporting.
     */
    @Test
    public void testSnippetValidation() throws Exception {
        DiseaseCase ARS = getArs();

        String correctSnippet = "TATCTT[A/CC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(correctSnippet)).build();
        ValidationResult result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, result.getMessage());

        String missingSnippet = "";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingSnippet)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Snippet wasn't entered!", result.getMessage());

        String missingLeftSeq = "[A/CC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingLeftSeq)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find upstream sequence in snippet: [A/CC]AGGCTTTT",
                result.getMessage());

        String missingLeftBracket = "TA/CC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingLeftBracket)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find opening bracket in snippet: TA/CC]AGGCTTTT",
                result.getMessage());

        String missingRightBracket = "TATCTT[A/CCAGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingRightBracket)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find closing bracket in snippet: TATCTT[A/CCAGGCTTTT",
                result.getMessage());

        String missingRightSeq = "TATCTT[A/CC]";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingRightSeq)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find downstream sequence in snippet: TATCTT[A/CC]",
                result.getMessage());

        String missingSlash = "TATCTT[ACC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingSlash)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Could not find proper slash (/) in snippet: TATCTT[ACC]AGGCTTTT", result.getMessage());

        String missingRef = "TATCTT[/CC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingRef)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for REF in snippet: TATCTT[/CC]AGGCTTTT",
                result.getMessage());

        String missingAlt = "TATCTT[A/]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(missingAlt)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Malformed (null) string for ALT in snippet: TATCTT[A/]AGGCTTTT", result.getMessage());

        String incorrectRef = "TATCTT[G/CC]AGGCTTTT";
        ARS = ARS.toBuilder().setVariant(0, ARS.getVariant(0).toBuilder().setSnippet(incorrectRef)).build();
        result = validator.validateDiseaseCase(ARS);
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Reference sequence (A) not given correctly in snippet (G): TATCTT[G/CC]AGGCTTTT", result.getMessage());
    }


    /**
     * Test correct validation of CSS snippet & position.
     */
    @Test
    public void testCrypticSpliceSiteValidation() throws Exception {
        DiseaseCase ARS = getArs(); // here, no CSS info is set by default, validation checked in other tests
        Variant V = ARS.getVariant(0);
        DiseaseCase ARS_CLEAN = ARS.toBuilder().clearVariant().build();


        ValidationResult result = validator.validateDiseaseCase(
                DiseaseCase.newBuilder(ARS_CLEAN).addVariant(
                        V.toBuilder().setCrypticPosition(10499)
                                .setCrypticSpliceSiteSnippet("GGCTTTTG|AGAAAAAAC")
                                .setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME)).build());
        assertEquals(ValidationResult.PASSED, result);
        assertEquals(validator.OKAY, result.getMessage());


        result = validator.validateDiseaseCase(ARS_CLEAN.toBuilder()
                .addVariant(V.toBuilder().setCrypticPosition(10500))
                .build());
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTG, Genomic sequence: GCTTTTGA",
                result.getMessage());

        result = validator.validateDiseaseCase(ARS_CLEAN.toBuilder()
                .addVariant(V.toBuilder().setCrypticPosition(10499).setCrypticSpliceSiteSnippet("GGCTTTTGAGAAAAAAC").build())
                .build());
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Unable to find '|' symbol in CSS snippet GGCTTTTGAGAAAAAAC",
                result.getMessage());

        result = validator.validateDiseaseCase(ARS_CLEAN.toBuilder()
                .addVariant(V.toBuilder().setCrypticPosition(10499).setCrypticSpliceSiteSnippet("GGCTTTTGA|GAAAAAAC").build())
                .build());
        assertEquals(ValidationResult.FAILED, validator.validateDiseaseCase(ARS));
        assertEquals("Prefix of CSS snippet: GGCTTTTGA, Genomic sequence: AGGCTTTTG",
                result.getMessage());


        result = validator.validateDiseaseCase(ARS_CLEAN.toBuilder()
                .addVariant(V.toBuilder().setCrypticPosition(10499).setCrypticSpliceSiteSnippet("GGCTTTT|GAGAAAAAAC").build())
                .build());
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
        DiseaseCase ARS = getArs();
        Variant V = ARS.getVariant(0);
        assertEquals(ValidationResult.PASSED, validator.variantValid(V.toBuilder()
                .setRefAllele("AA")
                .setAltAllele("CC")
                .setSnippet("TTCTATCTT[AA/CC]GGCTTT")
                .build())); // chr7:g.10490AA>CC

//        variant.setReferenceAllele("AA");
//        variant.setAlternateAllele("CCG");
//        variant.setSnippet("TTCTATCTT[AA/CCG]GGCTTT");
        assertEquals(ValidationResult.PASSED, validator.variantValid(V.toBuilder()
                .setRefAllele("AA")
                .setAltAllele("CCG")
                .setSnippet("TTCTATCTT[AA/CCG]GGCTTT")
                .build())); // chr7:g.10490AA>CCG

//        variant.setReferenceAllele("AA");
//        variant.setAlternateAllele("C");
//        variant.setSnippet("TTCTATCTT[AA/C]GGCTTT");
        assertEquals(ValidationResult.PASSED, validator.variantValid(V.toBuilder()
                .setRefAllele("AA")
                .setAltAllele("C")
                .setSnippet("TTCTATCTT[AA/C]GGCTTT")
                .build())); // chr7:g.10490AA>C
    }
}