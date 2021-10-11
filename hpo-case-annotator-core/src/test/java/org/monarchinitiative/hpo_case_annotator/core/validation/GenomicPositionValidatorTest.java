package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * At first two real-life model files are tested and then invalid cases are tested.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class GenomicPositionValidatorTest {

//    @Rule
//    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private GenomeAssemblies assemblies;

    @Mock
    private SequenceDao hg19SequenceDao;

    /**
     * Tested instance.
     */
    private GenomicPositionValidator validator;


    @BeforeEach
    public void setUp() throws Exception {
        assemblies = Mockito.mock(GenomeAssemblies.class);
        hg19SequenceDao = Mockito.mock(SequenceDao.class);
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

}