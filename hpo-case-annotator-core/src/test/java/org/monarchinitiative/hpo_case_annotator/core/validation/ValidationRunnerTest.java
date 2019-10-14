package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.monarchinitiative.hpo_case_annotator.core.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ValidationRunnerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private GenomeAssemblies assemblies;

    @Mock
    private SequenceDao hg19SequenceDao;

    /**
     * Tested instance.
     */
    private ValidationRunner<DiseaseCase> runner;


    @Before
    public void setUp() throws Exception {
        runner = ValidationRunner.forAllValidations(assemblies);
    }


    /**
     * Test validation real-life model.
     */
    @Test
    public void validateSingleModel() throws Exception {
        when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        when(hg19SequenceDao.fetchSequence("13", 31843348, 31843349)).thenReturn("A"); // expectedRefAllele
        when(hg19SequenceDao.fetchSequence("13", 31843343, 31843354)).thenReturn("TTTCTAGGCTT"); // 0-based numbering for both begin and end coordinates

        final DiseaseCase diseaseCase = DiseaseCaseModelExample.benMahmoud2013B3GLCT();

        final List<ValidationResult> results = runner.validateSingleModel(diseaseCase);

        // model valid, no failures are produced
        assertTrue(results.isEmpty());
    }

    @Test
    public void validateModelWithStructuralVariant() {
        when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));

        when(hg19SequenceDao.fetchSequence("13", 31843348, 31843349)).thenReturn("A"); // expectedRefAllele
        when(hg19SequenceDao.fetchSequence("13", 31843343, 31843354)).thenReturn("TTTCTAGGCTT"); // 0-based numbering for both begin and end coordinates

        DiseaseCase diseaseCase = DiseaseCaseModelExample.structural_beygo_2012_TCOF1_M18662();

        List<ValidationResult> results = runner.validateSingleModel(diseaseCase);

        System.out.println(results);
    }

    @Test
    public void validateMultipleModelsAtOnce() {
        // TODO - test validation of multiple models at once
    }
}