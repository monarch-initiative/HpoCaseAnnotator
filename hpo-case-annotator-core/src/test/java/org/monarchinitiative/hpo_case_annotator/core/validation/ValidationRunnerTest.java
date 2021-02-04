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
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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


    private static DiseaseCase buildCase(Iterable<? extends Variant> variants) {
        return DiseaseCase.newBuilder()
                .setPublication(
                        Publication.newBuilder()
                                .setAuthorList("Beygo J, Buiting K, Seland S, Lüdecke HJ, Hehr U, Lich C, Prager B, Lohmann DR, Wieczorek D")
                                .setTitle("First Report of a Single Exon Deletion in TCOF1 Causing Treacher Collins Syndrome")
                                .setJournal("Mol Syndromol")
                                .setYear("2012")
                                .setVolume("2(2)")
                                .setPages("53-59")
                                .setPmid("22712005")
                                .build())
                .setMetadata("Authors describe a proband M18662 with presence of TCS1")
                .setGene(Gene.newBuilder()
                        .setSymbol("GENE")
                        .setEntrezId(1234)
                        .build())
                /* Variants which belong to this model */
                .addAllVariant(variants)
                /* Family/proband information */
                .setFamilyInfo(FamilyInfo.newBuilder()
                        .setFamilyOrProbandId("M18662")
                        .setSex(Sex.FEMALE)
                        .setAge("P26Y")
                        .build())
                /* HPO terms */
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0011453")
                        .setLabel("Abnormality of the incus")
                        .setNotObserved(false)
                        .build())
                .setDisease(Disease.newBuilder()
                        .setDatabase("OMIM")
                        .setDiseaseId("123456")
                        .setDiseaseName("UNSPECIFIED SYNDROME PLACEHOLDER")
                        .build())
                /* Biocurator */
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:walterwhite")
                        .build())
                .setSoftwareVersion("1.2.3")
                .build();
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

        DiseaseCase diseaseCase = DiseaseCaseModelExample.benMahmoud2013B3GLCT();

        List<ValidationResult> results = runner.validateSingleModel(diseaseCase);

        // model valid, no failures are produced
        assertTrue(results.isEmpty());
    }

    @Test
    public void validateSingleModel_del() {
        when(assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)).thenReturn(true);
        when(assemblies.getSequenceDaoForAssembly(GenomeAssembly.GRCH_37)).thenReturn(Optional.of(hg19SequenceDao));

        when(hg19SequenceDao.fetchSequence("13", 31843348, 31843349)).thenReturn("A"); // expectedRefAllele
        when(hg19SequenceDao.fetchSequence("13", 31843343, 31843354)).thenReturn("TTTCTAGGCTT"); // 0-based numbering for both begin and end coordinates

        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37).setContig("5").setPos(149741531).setPos2(149744897)
                        .setRefAllele("N").setAltAllele("DEL")
                        .setCiBeginOne(-5).setCiBeginTwo(5).setCiEndOne(-15).setCiEndTwo(10)
                        .build())
                .setVariantClass("structural")
                .setSvType(StructuralType.DEL)
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                        .build())
                .build());
        DiseaseCase diseaseCase = buildCase(variants);

        List<ValidationResult> results = runner.validateSingleModel(diseaseCase);
        System.err.println(results);
        assertThat(results, is(empty()));
    }

    @Test
    public void validateMultipleModelsAtOnce() {
        // TODO - test validation of multiple models at once
    }

    @Test
    public void validateSingleModel_breakend() {
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.GRCH_37).setContig("2").setPos(1234).setContig2("3").setPos2(5678)
                        .setRefAllele("N").setAltAllele("ACT")
                        .setCiBeginOne(-5).setCiBeginTwo(5).setCiEndOne(-15).setCiEndTwo(10)
                        .build())
                .setVariantClass("structural")
                .setSvType(StructuralType.BND)
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.TRANSLOCATION)
                        .build())
                .build());
        DiseaseCase diseaseCase = buildCase(variants);

        List<ValidationResult> results = runner.validateSingleModel(diseaseCase);

        assertThat(results, is(empty()));
    }
}