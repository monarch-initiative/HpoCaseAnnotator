package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * At first two real-life model files are tested and then invalid cases are tested.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class GenomicPositionValidatorTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private GenomeAssemblies assemblies;

    @Mock
    private SequenceDao hg19SequenceDao;

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
            return XMLModelParser.loadDiseaseCase(is)
                    .orElseThrow(() -> new Exception("Unable to read test data from /models/xml/Ars-2000-NF1-95-89.xml"));
        }
    }


    private static DiseaseCase getHull() throws Exception {
        try (InputStream is = GenomicPositionValidatorTest.class.getResourceAsStream("/models/xml/Hull-1994-CFTR.xml")) {
            return XMLModelParser.loadDiseaseCase(is)
                    .orElseThrow(() -> new Exception("Unable to read test data from /models/xml/Hull-1994-CFTR.xml"));
        }
    }

    @Before
    public void setUp() throws Exception {
        validator = new GenomicPositionValidator(assemblies);
    }

    @Test
    public void variantWithUnknownGenomeAssembly() throws Exception {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.HG_38))
                .thenReturn(false);

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_38)
                        .setContig("chr1")
                        .setPos(1234)
                        .setRefAllele("C")
                        .setAltAllele("A")
                        .build())
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Fasta file for genome assembly GRCH_38 is not present")));
    }

    @Test
    public void validateCorrectVariant() throws Exception {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder() // mix of the upper and lower case nucleotides is intentional
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("a")
                        .setAltAllele("Cc")
                        .build())
                .setSnippet("TAtcTT[a/Cc]AGgCtTTT")
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(0));
    }

    @Test
    public void validateBadRefAllele() {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("G")
                        .setAltAllele("CC")
                        .build())
                .setSnippet("TATCTT[G/CC]AGGCTTTT")
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(2));
        assertThat(results, hasItems(ValidationResult.fail("Ref sequence 'G' does not match the sequence 'A' observed at 'chr7:10489-10490'"),
                ValidationResult.fail("Ref sequence in snippet 'G' does not match the actual sequence 'A' observed at interval 'chr7:10489-10490'")));
    }

    @Test
    public void validateBadPrefix() {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("A")
                        .setAltAllele("CC")
                        .build())
                .setSnippet("TTTCTT[A/CC]AGGCTTTT")
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Prefix in snippet 'TTTCTT' does not match the actual sequence 'TATCTT' observed at interval 'chr7:10483-10489'")));
    }

    @Test
    public void validateBadSuffix() {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("A")
                        .setAltAllele("CC")
                        .build())
                .setSnippet("TATCTT[A/CC]TGGCTTTT")
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Suffix in snippet 'TGGCTTTT' does not match the actual sequence 'AGGCTTTT' observed at interval 'chr7:10490-10498'")));
    }

    @Test
    public void validateVariantWithNoRefAndAltAlleles() {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
//        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele won't be checked
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("")
                        .setAltAllele("")
                        .build())
                .setSnippet("TATCTT[A/CC]AGGCTTTT")
                .build();

        final List<ValidationResult> results = validator.validate(variant);
//        results.forEach(System.err::println);
        assertThat(results.size(), is(2));
        assertThat(results, hasItems(ValidationResult.fail("Ref sequence not initialized (length=0)"),
                ValidationResult.fail("Alt sequence not initialized (length=0)")));
    }

    @Test
    public void validateVariantWithInvalidRefAllele() {
        Mockito.when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        Mockito.when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10489, 10490)).thenReturn("A"); // expectedRefAllele
        Mockito.when(hg19SequenceDao.fetchSequence("chr7", 10483, 10498)).thenReturn("TATCTTAAGGCTTTT");

        Variant variant = Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37)
                        .setContig("chr7")
                        .setPos(10490)
                        .setRefAllele("G")
                        .setAltAllele("CC")
                        .build())
                .setSnippet("TATCTT[A/CC]AGGCTTTT") // ref is good in snippet
                .build();

        final List<ValidationResult> results = validator.validate(variant);
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Ref sequence 'G' does not match the sequence 'A' observed at 'chr7:10489-10490'")));
    }


    @Test
    public void validateStructuralVariant() {
        // this validator does not validate structural variants, since they do not contain neither ref, alt, nor snippet
        List<ValidationResult> results = validator.validate(Variant.newBuilder()
                .setVariantClass("structural")
                .build());

        assertThat(results, is(Collections.emptyList()));
    }

    /**
     * Test validation of correct real-life model.
     */
    @Test
    @Ignore
    public void validateDiseaseCaseHull() throws Exception {
//        ValidationResult result = validator.validateDiseaseCase(getHull());
//        assertEquals(ValidationResult.PASSED, result);
//        assertEquals(validator.OKAY, result.getMessage());
    }


    /**
     * Test validation of correct real-life model.
     */
    @Test
    @Ignore
    public void validateDiseaseCaseArs() throws Exception {
//        ValidationResult result = validator.validateDiseaseCase(getArs());
//        assertEquals(ValidationResult.PASSED, result);
//        assertEquals(validator.OKAY, result.getMessage());
    }


    /**
     * Test validation of incorrect REF allele.
     */
    @Test
    @Ignore
    public void testInvalidAllele() throws Exception {
        DiseaseCase ARS = getArs();

        ARS = ARS.toBuilder().setVariant(0, Variant.newBuilder(ARS.getVariant(0)).setRefAllele("GG").build()).build();
//        ValidationResult result = validator.validateDiseaseCase(ARS);
//        assertEquals(ValidationResult.FAILED, result);
//        assertEquals("genomic reference=\"AA\" at chr7:10490-10491; entered ref=GG/alt=CC/len=2", result.getMessage());
    }


    /**
     * Test correct snippet validation and error reporting.
     */
    @Test
    @Ignore
    public void testSnippetValidation() throws Exception {
        DiseaseCase ARS = getArs();

        /*String correctSnippet = "TATCTT[A/CC]AGGCTTTT";
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
        assertEquals("Reference sequence (A) not given correctly in snippet (G): TATCTT[G/CC]AGGCTTTT", result.getMessage());*/
    }


    /**
     * Test correct validation of CSS snippet & position.
     */
    @Test
    @Ignore
    public void testCrypticSpliceSiteValidation() throws Exception {
        DiseaseCase ARS = getArs(); // here, no CSS info is set by default, validation checked in other tests
        Variant V = ARS.getVariant(0);
        DiseaseCase ARS_CLEAN = ARS.toBuilder().clearVariant().build();

/*
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
                result.getMessage());*/
    }


    /**
     * Test the variants chr7:g.10490AA>CC, chr7:g.10490AA>CCG
     *
     * @throws Exception blah
     */
    @Test
    @Ignore
    public void testDelInsVariant() throws Exception {
        /*DiseaseCase ARS = getArs();
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
                */
    }
}