package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.test.TestData;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class ValidationRunnerTest {

    @Mock
    private GenomeAssemblies assemblies;

    @Mock
    private SequenceDao hg19SequenceDao;

    private ValidationRunner<DiseaseCase> runner;


    @BeforeEach
    public void setUp() throws Exception {
        assemblies = Mockito.mock(GenomeAssemblies.class);
        hg19SequenceDao = Mockito.mock(SequenceDao.class);
        runner = ValidationRunner.forAllValidations(assemblies);
    }

    @Test
    public void validateSingleModel() throws Exception {
        when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));
        when(hg19SequenceDao.fetchSequence("9", 123_737_056, 123_737_057)).thenReturn("C"); // expectedRefAllele
        when(hg19SequenceDao.fetchSequence("9", 123_737_047, 123_737_066)).thenReturn("TTCATTTACCTCTACTGGC"); // 0-based numbering for both begin and end coordinates

        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();
        DiseaseCase dcc = DiseaseCase.newBuilder(diseaseCase)
                .clearVariant()
                .addVariant(diseaseCase.getVariant(0))
                .build();


        List<ValidationResult> results = runner.validateSingleModel(dcc);

        // model valid, no failures are produced
        assertThat(results.isEmpty(), equalTo(true));
    }

}