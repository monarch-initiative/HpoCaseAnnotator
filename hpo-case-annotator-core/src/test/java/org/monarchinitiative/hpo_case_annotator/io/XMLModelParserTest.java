package org.monarchinitiative.hpo_case_annotator.io;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class XMLModelParserTest {


    /**
     * Tested instance.
     */
    private XMLModelParser parser;


    @Before
    public void setUp() throws Exception {
        parser = new XMLModelParser(TestResources.TEST_MODEL_FILE_DIR);
    }


    /**
     * This method programmatically creates complete model instance for testing. The model contains three variants: one
     * Splicing variant, one Mendelian variant and one Somatic variant with all fields initialized. <p>The data of model
     * are the same as those in XML file <em>src/test/resources/models/xml/Aguilar-Ramirez-2009-C5.xml</em>
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private static DiseaseCaseModel prepareModel() {
        DiseaseCaseModel model = new DiseaseCaseModel();

        /* Genome build */
        model.setGenomeBuild("GRCh37");

        /* Publication data */
        model.setPublication(
                new Publication("Aguilar-Ramirez P, Reis ES, Florido MP, Barbosa AS, Farah CS, Costa-Carvalho BT, " +
                        "Isaac L", "Skipping of exon 30 in C5 gene results in complete human C5 deficiency and " +
                        "demonstrates the importance of C5d and CUB domains for stability", "Mol Immunol", "2009",
                        "46(10)", "2116-23", "19375167"));

        /* Metadata */
        model.setMetadata(new Metadata("Authors describe proband coming from large family with history of consanguinity carrying " +
                "primary complete C5 deficiency. Blah, blah..."));

        /* Gene under examination*/
        model.setTargetGene(new TargetGene("C5", "727", "9"));

        /* Variants which belong to this model */
        SplicingVariant first = new SplicingVariant();
        first.setChromosome("9");
        first.setPosition("123737057");
        first.setReferenceAllele("C");
        first.setAlternateAllele("T");
        first.setSnippet("TTCATTTAC[C/T]TCTACTGG");
        first.setGenotype("homozygous");
        first.setVariantClass("splicing");
        first.setPathomechanism("splicing|3css|activated");
        first.setCosegregation("yes");
        first.setComparability("no");
        first.setConsequence("Exon skipping");
        first.setCrypticPosition("123737090");
        first.setCrypticSpliceSiteType("5' splice site");
        first.setCrypticSpliceSiteSnippet("ATATG|GCGAGTTCTT");
        first.setSplicingValidation(new SplicingValidation(true, false, true, false, true, true, false,
                false, true));
        model.getVariants().add(first);

        MendelianVariant second = new MendelianVariant();
        second.setAlternateAllele("G");
        second.setChromosome("9");
        second.setComparability("no");
        second.setCosegregation("no");
        second.setEmsaGeneId("TF_GENEID_TEST");
        second.setEmsaTFSymbol("TF_SYMBOL_TEST");
        second.setEmsaValidationPerformed("yes");
        second.setGenotype("homozygous");
        second.setOther("down");
        second.setOtherEffect("in vitro mRNA expression assay");
        second.setPathomechanism("promoter|reduced-transcription");
        second.setPosition("123737057");
        second.setReferenceAllele("C");
        second.setRegulator("TEST_REGULATOR");
        second.setReporterRegulation("no");
        second.setReporterResidualActivity("RES_ACT");
        second.setSnippet("TTCATTTAC[C/G]TCTACTGG");
        second.setVariantClass("promoter");
        model.getVariants().add(second);

        SomaticVariant third = new SomaticVariant();
        third.setMPatients("100");
        third.setNPatients("78");
        third.setAlternateAllele("A");
        third.setChromosome("9");
        third.setEmsaGeneId("TF_GENE_SYMBOL_TEST");
        third.setEmsaTFSymbol("TF_EMSA_SOM_TEST");
        third.setEmsaValidationPerformed("no");
        third.setGenotype("heterozygous");
        third.setOther("up");
        third.setOtherEffect("Transgenic model");
        third.setPathomechanism("5UTR|transcription");
        third.setPosition("123737057");
        third.setReferenceAllele("C");
        third.setRegulator("SOMATIC_REGULATOR");
        third.setReporterRegulation("up");
        third.setReporterResidualActivity("SOMATIC_RP_PERC");
        third.setSnippet("TTCATTTAC[C/A]TCTACTGG");
        third.setVariantClass("5UTR");
        model.getVariants().add(third);

        /* Family/proband information */
        model.setFamilyInfo(new FamilyInfo("II:9", "male", "19"));

        /* HPO terms */
        model.getHpoList().addAll(Arrays.asList(
                new HPO("HP:0001287", "Meningitis", "YES"),
                new HPO("HP:0030955", "Alcoholism", "NOT"),
                new HPO("HP:0002721", "Immunodeficiency", "YES")));

        /* Disease-related information */
        model.setDisease(new Disease("OMIM", "609536", "COMPLEMENT COMPONENT 5 DEFICIENCY; C5D"));

        /* Biocurator */
        model.setBiocurator(new Biocurator("HPO:walterwhite"));

        return model;
    }


    /**
     * Utility method for reading of whole file into the String.
     *
     * @param filepath path to file about to be read.
     * @return String with content of the file.
     */
    private static String readFileContent(String filepath) throws Exception {
        Path fpath = Paths.get(filepath);
        return Files.lines(fpath).collect(Collectors.joining("\n"));
    }


    /**
     * Test that parser identifies 4 model files in test model dir.
     */
    @Test
    public void testInitialization() throws Exception {
        assertEquals(4, parser.getModelNames().size());
        assertEquals(".xml", XMLModelParser.MODEL_SUFFIX);
    }


    /**
     * Test correct serialization of DiseaseCaseModel attributes into XML format.
     */
    @Test
    @Ignore("Writing of XML using XMLEncoder is dependent on minor & patch version of used JDK.")
    public void saveModel() throws Exception {
//        DiseaseCaseModel model = prepareModel();
//        StringWriter writer = new StringWriter();
//        parser.saveModel(writer, model);
//
//        String expected = readFileContent("target/test-classes/models/xml/Aguilar-Ramirez-2009-C5.xml");
//        assertEquals(expected, writer.toString());
    }


    /**
     * Thorough testing of attributes of model deserialized from <em>Aguilar-Ramirez-2009-C5.xml</em> file.
     */
    @Test
    public void readModel() throws Exception {
        DiseaseCaseModel model;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Aguilar-Ramirez-2009-C5.xml")) {
            model = parser.readModel(is);
        }

        // Biocurator
        assertEquals("HPO:walterwhite", model.getBiocurator().getBioCuratorId());

        // Disease
        assertEquals("OMIM", model.getDisease().getDatabase());
        assertEquals("609536", model.getDisease().getDiseaseId());
        assertEquals("COMPLEMENT COMPONENT 5 DEFICIENCY; C5D", model.getDisease().getDiseaseName());

        // FamilyInfo
        assertEquals("19", model.getFamilyInfo().getAge());
        assertEquals("II:9", model.getFamilyInfo().getFamilyOrPatientID());
        assertEquals("male", model.getFamilyInfo().getSex());

        // Genome Build
        assertEquals("GRCh37", model.getGenomeBuild());

        // HPO terms
        List<HPO> hpoList = model.getHpoList();
        assertEquals(new HPO("HP:0001287", "Meningitis", "YES"), hpoList.get(0));
        assertEquals(new HPO("HP:0030955", "Alcoholism", "NOT"), hpoList.get(1));
        assertEquals(new HPO("HP:0002721", "Immunodeficiency", "YES"), hpoList.get(2));

        // Metadata
        assertEquals("Authors describe proband coming from large family with history of consanguinity carrying " +
                "primary complete C5 deficiency. Blah, blah...", model.getMetadata().getMetadataText());

        // Publication
        assertEquals(new Publication("Aguilar-Ramirez P, Reis ES, Florido MP, Barbosa AS, Farah CS, Costa-Carvalho BT, Isaac L",
                "Skipping of exon 30 in C5 gene results in complete human C5 deficiency and demonstrates the importance of C5d and CUB domains for stability",
                "Mol Immunol", "2009", "46(10)", "2116-23", "19375167"), model.getPublication());

        // Target Gene
        assertEquals(new TargetGene("C5", "727", "9"), model.getTargetGene());

        // Variants:
        // Splicing variant
        List<Variant> variants = model.getVariants();
        SplicingVariant splicing = (SplicingVariant) variants.get(0);
        assertEquals("T", splicing.getAlternateAllele());
        assertEquals("9", splicing.getChromosome());
        assertEquals("123737090", splicing.getCrypticPosition());
        assertEquals("ATATG|GCGAGTTCTT", splicing.getCrypticSpliceSiteSnippet());
        assertEquals("5' splice site", splicing.getCrypticSpliceSiteType());
        assertEquals("no", splicing.getComparability());
        assertEquals("Exon skipping", splicing.getConsequence());
        assertEquals("yes", splicing.getCosegregation());
        assertEquals("homozygous", splicing.getGenotype());
        assertEquals("splicing|3css|activated", splicing.getPathomechanism());
        assertEquals("123737057", splicing.getPosition());
        assertEquals("C", splicing.getReferenceAllele());
        assertEquals("TTCATTTAC[C/T]TCTACTGG", splicing.getSnippet());
        assertEquals(new SplicingValidation(true, false, true, false, true, true, false, false, true),
                splicing.getSplicingValidation());
        assertEquals("splicing", splicing.getVariantClass());

        // Mendelian variant
        MendelianVariant mendelian = (MendelianVariant) variants.get(1);
        assertEquals("G", mendelian.getAlternateAllele());
        assertEquals("9", mendelian.getChromosome());
        assertEquals("no", mendelian.getComparability());
        assertEquals("no", mendelian.getCosegregation());
        assertEquals("TF_GENEID_TEST", mendelian.getEmsaGeneId());
        assertEquals("TF_SYMBOL_TEST", mendelian.getEmsaTFSymbol());
        assertEquals("yes", mendelian.getEmsaValidationPerformed());
        assertEquals("homozygous", mendelian.getGenotype());
        assertEquals("down", mendelian.getOther());
        assertEquals("in vitro mRNA expression assay", mendelian.getOtherEffect());
        assertEquals("promoter|reduced-transcription", mendelian.getPathomechanism());
        assertEquals("123737057", mendelian.getPosition());
        assertEquals("C", mendelian.getReferenceAllele());
        assertEquals("TEST_REGULATOR", mendelian.getRegulator());
        assertEquals("no", mendelian.getReporterRegulation());
        assertEquals("RES_ACT", mendelian.getReporterResidualActivity());
        assertEquals("TTCATTTAC[C/G]TCTACTGG", mendelian.getSnippet());
        assertEquals("promoter", mendelian.getVariantClass());

        // Somatic variant
        SomaticVariant somatic = (SomaticVariant) variants.get(2);
        assertEquals("100", somatic.getMPatients());
        assertEquals("78", somatic.getNPatients());
        assertEquals("A", somatic.getAlternateAllele());
        assertEquals("9", somatic.getChromosome());
        assertEquals("TF_GENE_SYMBOL_TEST", somatic.getEmsaGeneId());
        assertEquals("TF_EMSA_SOM_TEST", somatic.getEmsaTFSymbol());
        assertEquals("no", somatic.getEmsaValidationPerformed());
        assertEquals("heterozygous", somatic.getGenotype());
        assertEquals("up", somatic.getOther());
        assertEquals("Transgenic model", somatic.getOtherEffect());
        assertEquals("5UTR|transcription", somatic.getPathomechanism());
        assertEquals("123737057", somatic.getPosition());
        assertEquals("C", somatic.getReferenceAllele());
        assertEquals("SOMATIC_REGULATOR", somatic.getRegulator());
        assertEquals("up", somatic.getReporterRegulation());
        assertEquals("SOMATIC_RP_PERC", somatic.getReporterResidualActivity());
        assertEquals("TTCATTTAC[C/A]TCTACTGG", somatic.getSnippet());
        assertEquals("5UTR", somatic.getVariantClass());
    }


    /**
     * Test correct deserialization of some attributes from Davidson-2010-BEST1.xml file.
     */
    @Test
    public void readFirstModel() throws Exception {
        DiseaseCaseModel davidson;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Davidson-2010-BEST1.xml")) {
            davidson = parser.readModel(is);
        }

        assertEquals("OMIM", davidson.getDisease().getDatabase());
        assertEquals("611809", davidson.getDisease().getDiseaseId());
        assertEquals("BESTROPHINOPATHY, AUTOSOMAL RECESSIVE; ARB", davidson.getDisease().getDiseaseName());
        assertEquals("Subject 1", davidson.getFamilyInfo().getFamilyOrPatientID());
        assertEquals("GRCh37", davidson.getGenomeBuild());
        assertEquals(6, davidson.getHpoList().size());

        assertEquals("Davidson AE, Sergouniotis PI, Burgess-Mullan R, Hart-Holden N, Low S, Foster PJ, Manson FD, " +
                "Black GC, Webster AR", davidson.getPublication().getAuthorlist());
        assertEquals("Mol Vis", davidson.getPublication().getJournal());
        assertEquals("2916-22", davidson.getPublication().getPages());
        assertEquals("21203346", davidson.getPublication().getPmid());
        assertEquals("A synonymous codon variant in two patients with autosomal recessive bestrophinopathy alters in" +
                " vitro splicing of BEST1", davidson.getPublication().getTitle());
        assertEquals("16", davidson.getPublication().getVolume());
        assertEquals("2010", davidson.getPublication().getYear());

        assertEquals("BEST1", davidson.getTargetGene().getGeneName());
        assertEquals("7439", davidson.getTargetGene().getEntrezID());

        assertEquals(2, davidson.getVariants().size());
    }


    /**
     * Test correct deserialization of some attributes from Ars-2000-NF1-95-89.xml file.
     */
    @Test
    public void readSecondModel() throws Exception {
        DiseaseCaseModel ars;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Ars-2000-NF1-95-89.xml")) {
            ars = parser.readModel(is);
        }

        assertEquals("HPO:lccarmody", ars.getBiocurator().getBioCuratorId());

        assertEquals("162200", ars.getDisease().getDiseaseId());
        assertEquals("NEUROFIBROMATOSIS, TYPE I; NF1", ars.getDisease().getDiseaseName());

        assertEquals("Mutations affecting mRNA splicing are the most common molecular defects in patients with neurofibromatosis type 1", ars.getPublication().getTitle());
        assertEquals("Ars E, Serra E, García J, Kruyer H, Gaona A, Lázaro C, Estivill X", ars.getPublication().getAuthorlist());

        assertEquals(1, ars.getVariants().size());

        assertTrue(ars.getVariants().get(0) instanceof SplicingVariant);
        SplicingVariant splicingVariant = (SplicingVariant) ars.getVariants().get(0);
        assertEquals("CC", splicingVariant.getAlternateAllele());
        assertEquals("A", splicingVariant.getReferenceAllele());
        assertEquals("7", splicingVariant.getChromosome());
        assertEquals("no", splicingVariant.getComparability());
        assertEquals("Exon skipping", splicingVariant.getConsequence());
        assertEquals("yes", splicingVariant.getCosegregation());
        assertEquals("heterozygous", splicingVariant.getGenotype());
        assertEquals("coding|missense", splicingVariant.getPathomechanism());
        assertEquals("10490", splicingVariant.getPosition());
        assertEquals("TATCTT[A/CC]AGGCTTTT", splicingVariant.getSnippet());
        assertEquals("coding", splicingVariant.getVariantClass());

        assertTrue(splicingVariant.getSplicingValidation().isRtPCRValidation());
    }


    /**
     * Test functionality of {@link XMLModelParser#getModelNames()} method. Check correct number of models and their
     * names.
     */
    @Test
    public void testGetModelNames() throws Exception {
        List<String> modelNames = parser.getModelNames().stream()
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(4, modelNames.size());
        assertEquals("Aguilar-Ramirez-2009-C5.xml", modelNames.get(0));
        assertEquals("Ars-2000-NF1-95-89.xml", modelNames.get(1));
        assertEquals("Davidson-2010-BEST1.xml", modelNames.get(2));
        assertEquals("Hull-1994-CFTR.xml", modelNames.get(3));
    }


}