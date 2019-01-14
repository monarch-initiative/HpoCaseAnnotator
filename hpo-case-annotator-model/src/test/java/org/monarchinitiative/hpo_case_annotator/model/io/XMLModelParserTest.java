package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class XMLModelParserTest {


    /**
     * Tested instance.
     */
    private XMLModelParser parser;


    /**
     * This method programmatically creates complete model instance for testing. The model contains three variants: one
     * Splicing variant, one Mendelian variant and one Somatic variant with all fields initialized. <p>The data of model
     * are the same as those in XML file <em>src/test/resources/models/xml/Aguilar-Ramirez-2009-C5.xml</em>
     *
     * @return {@link DiseaseCase} instance for testing.
     */
    private static DiseaseCase prepareModel() {

        /* Genome build */
        return DiseaseCase.newBuilder()
//                .setGenomeBuild("GRCh37")

                /* Publication data */
                .setPublication(
                        Publication.newBuilder().setAuthorList("Aguilar-Ramirez P, Reis ES, Florido MP, Barbosa AS, Farah CS, Costa-Carvalho BT, Isaac L")
                                .setTitle("Skipping of exon 30 in C5 gene results in complete human C5 deficiency and demonstrates the importance of C5d and CUB domains for stability")
                                .setJournal("Mol Immunol")
                                .setYear("2009")
                                .setVolume("46(10)")
                                .setPages("2116-23")
                                .setPmid("19375167")
                                .build())
                .setMetadata("Authors describe proband coming from large family with history of consanguinity carrying " +
                        "primary complete C5 deficiency. Blah, blah...")
                .setGene(Gene.newBuilder()
                        .setSymbol("C5")
                        .setEntrezId(727)
                        .build())
                /* Variants which belong to this model */
                .setVariant(0, Variant.newBuilder()
                        .setVariantPosition(VariantPosition.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("9")
                                .setPos(123737057)
                                .setRefAllele("C")
                                .setAltAllele("T")
                                .build())
                        .setSnippet("TTCATTTAC[C/T]TCTACTGG")
                        .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                        .setVariantClass("splicing")
                        .setPathomechanism("splicing|3css|activated")
                        .setConsequence("Exon skipping")
                        .setCrypticPosition(123737090)
                        .setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME)
                        .setCrypticSpliceSiteSnippet("ATATG|GCGAGTTCTT")
                        .setVariantValidation(VariantValidation.newBuilder()
                                .setCosegregation(true)
                                .setComparability(false)
                                .setMinigeneValidation(true)
                                .setRtPcrValidation(true)
                                .setSrProteinKnockdownValidation(true)
                                .setCDnaSequencingValidation(true)
                                .setOtherValidation(true)
                                .build())
                        .build())
                .setVariant(1, Variant.newBuilder()
                        .setVariantPosition(VariantPosition.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("9")
                                .setPos(123737057)
                                .setRefAllele("C")
                                .setAltAllele("G")
                                .build())
                        .setSnippet("TTCATTTAC[C/G]TCTACTGG")
                        .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                        .setVariantClass("promoter")
                        .setPathomechanism("promoter|reduced-transcription")
                        .setVariantValidation(VariantValidation.newBuilder()
                                .setComparability(false)
                                .setCosegregation(false)
                                .setEmsaValidationPerformed(true)
                                .setEmsaGeneId("TF_GENEID_TEST")
                                .setEmsaTfSymbol("TF_SYMBOL_TEST")
                                .setOtherChoices("down")
                                .setOtherEffect("in vitro mRNA expression assay")
                                .setRegulator("TEST_REGULATOR")
                                .setReporterRegulation("no")
                                .setReporterResidualActivity("RES_ACT")
                                .build())
                        .build())
                .setVariant(2, Variant.newBuilder()
                        .setVariantPosition(VariantPosition.newBuilder()
                                .setGenomeAssembly(GenomeAssembly.HG_19)
                                .setContig("9")
                                .setPos(123737057)
                                .setRefAllele("C")
                                .setAltAllele("A")
                                .build())
                        .setSnippet("TTCATTTAC[C/A]TCTACTGG")
                        .setGenotype(Genotype.HETEROZYGOUS)
                        .setVariantClass("5UTR")
                        .setPathomechanism("5UTR|transcription")
                        .setVariantValidation(VariantValidation.newBuilder()
                                .setEmsaGeneId("TF_GENE_SYMBOL_TEST")
                                .setEmsaTfSymbol("TF_EMSA_SOM_TEST")
                                .setMPatients(100)
                                .setNPatients(78)
                                .setOtherChoices("up")
                                .setOtherEffect("Transgenic model")
                                .setRegulator("SOMATIC_REGULATOR")
                                .setReporterRegulation("up")
                                .setReporterResidualActivity("SOMATIC_RP_PERC")
                                .build())
                        .build())
                /* Family/proband information */
                .setFamilyInfo(FamilyInfo.newBuilder()
                        .setFamilyOrProbandId("II:9")
                        .setSex(Sex.MALE)
                        .setAge("19")
                        .build())
                /* HPO terms */
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0001287")
                        .setLabel("Meningitis")
                        .setNotObserved(false)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0030955")
                        .setLabel("Alcoholism")
                        .setNotObserved(true)
                        .build())
                .addPhenotype(OntologyClass.newBuilder()
                        .setId("HP:0002721")
                        .setLabel("Immunodeficiency")
                        .setNotObserved(false)
                        .build())
                .setDisease(Disease.newBuilder()
                        .setDatabase("OMIM")
                        .setDiseaseId("609536")
                        .setDiseaseName("COMPLEMENT COMPONENT 5 DEFICIENCY; C5D")
                        .build())
                /* Biocurator */
                .setBiocurator(Biocurator.newBuilder()
                        .setBiocuratorId("HPO:walterwhite")
                        .build())
                .build();
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


    @Before
    public void setUp() throws Exception {
        parser = new XMLModelParser(new File(TestResources.TEST_XML_MODEL_FILE_DIR.getFile()));
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
        DiseaseCase model;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Aguilar-Ramirez-2009-C5.xml")) {
            model = parser.readModel(is).orElseThrow(() -> new Exception("Unable to decode the test data at /models/xml/Aguilar-Ramirez-2009-C5.xml"));
        }

        // Biocurator
        assertEquals("HPO:walterwhite", model.getBiocurator().getBiocuratorId());

        // Disease
        assertEquals("OMIM", model.getDisease().getDatabase());
        assertEquals("609536", model.getDisease().getDiseaseId());
        assertEquals("COMPLEMENT COMPONENT 5 DEFICIENCY; C5D", model.getDisease().getDiseaseName());

        // FamilyInfo
        assertEquals("19", model.getFamilyInfo().getAge());
        assertEquals("II:9", model.getFamilyInfo().getFamilyOrProbandId());
        assertEquals(Sex.MALE, model.getFamilyInfo().getSex());

        // Genome Build
//        assertEquals("GRCh37", model.getGenomeBuild());

        // HPO terms
        List<OntologyClass> hpoList = model.getPhenotypeList();
        assertEquals(OntologyClass.newBuilder().setId("HP:0001287").setLabel("Meningitis").setNotObserved(false).build(), hpoList.get(0));
        assertEquals(OntologyClass.newBuilder().setId("HP:0030955").setLabel("Alcoholism").setNotObserved(true).build(), hpoList.get(1));
        assertEquals(OntologyClass.newBuilder().setId("HP:0002721").setLabel("Immunodeficiency").setNotObserved(false).build(), hpoList.get(2));

        // Metadata
        assertEquals("Authors describe proband coming from large family with history of consanguinity carrying " +
                "primary complete C5 deficiency. Blah, blah...", model.getMetadata());

        // Publication
        assertEquals(Publication.newBuilder().setAuthorList("Aguilar-Ramirez P, Reis ES, Florido MP, Barbosa AS, Farah CS, Costa-Carvalho BT, Isaac L")
                        .setTitle("Skipping of exon 30 in C5 gene results in complete human C5 deficiency and demonstrates the importance of C5d and CUB domains for stability")
                        .setJournal("Mol Immunol")
                        .setYear("2009")
                        .setVolume("46(10)")
                        .setPages("2116-23")
                        .setPmid("19375167").build(),
                model.getPublication());

        // Target Gene
        assertEquals(Gene.newBuilder().setEntrezId(727).setSymbol("C5").build(), model.getGene());

        // Variants:
        // Splicing variant
        assertEquals(Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123737057)
                        .setRefAllele("C")
                        .setAltAllele("T")
                        .build())
                .setSnippet("TTCATTTAC[C/T]TCTACTGG")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("splicing")
                .setPathomechanism("splicing|3css|activated")
                .setConsequence("Exon skipping")
                .setCrypticPosition(123737090)
                .setCrypticSpliceSiteType(CrypticSpliceSiteType.FIVE_PRIME)
                .setCrypticSpliceSiteSnippet("ATATG|GCGAGTTCTT")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setCosegregation(true)
                        .setComparability(false)
                        .setMinigeneValidation(true)
                        .setRtPcrValidation(true)
                        .setSrProteinKnockdownValidation(true)
                        .setCDnaSequencingValidation(true)
                        .setOtherValidation(true)
                        .build())
                .build(), model.getVariant(0));

        // Mendelian variant
        assertEquals(Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123737057)
                        .setRefAllele("C")
                        .setAltAllele("G")
                        .build())
                .setSnippet("TTCATTTAC[C/G]TCTACTGG")
                .setGenotype(Genotype.HOMOZYGOUS_ALTERNATE)
                .setVariantClass("promoter")
                .setPathomechanism("promoter|reduced-transcription")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.MENDELIAN)
                        .setComparability(false)
                        .setCosegregation(false)
                        .setEmsaValidationPerformed(true)
                        .setEmsaGeneId("TF_GENEID_TEST")
                        .setEmsaTfSymbol("TF_SYMBOL_TEST")
                        .setOtherChoices("down")
                        .setOtherEffect("in vitro mRNA expression assay")
                        .setRegulator("TEST_REGULATOR")
                        .setReporterRegulation("no")
                        .setReporterResidualActivity("RES_ACT")
                        .build())
                .build(), model.getVariant(1));

        // Somatic variant
        assertEquals(Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(GenomeAssembly.HG_19)
                        .setContig("9")
                        .setPos(123737057)
                        .setRefAllele("C")
                        .setAltAllele("A")
                        .build())
                .setSnippet("TTCATTTAC[C/A]TCTACTGG")
                .setGenotype(Genotype.HETEROZYGOUS)
                .setVariantClass("5UTR")
                .setPathomechanism("5UTR|transcription")
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SOMATIC)
                        .setEmsaGeneId("TF_GENE_SYMBOL_TEST")
                        .setEmsaTfSymbol("TF_EMSA_SOM_TEST")
                        .setMPatients(100)
                        .setNPatients(78)
                        .setOtherChoices("up")
                        .setOtherEffect("Transgenic model")
                        .setRegulator("SOMATIC_REGULATOR")
                        .setReporterRegulation("up")
                        .setReporterResidualActivity("SOMATIC_RP_PERC")
                        .build())
                .build(), model.getVariant(2));
    }


    /**
     * Test correct deserialization of some attributes from Davidson-2010-BEST1.xml file.
     */
    @Test
    public void readFirstModel() throws Exception {
        DiseaseCase davidson;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Davidson-2010-BEST1.xml")) {
            davidson = parser.readModel(is).orElseThrow(() -> new Exception("Unable to decode the test data at /models/xml/Davidson-2010-BEST1.xml"));
        }

        assertEquals("OMIM", davidson.getDisease().getDatabase());
        assertEquals("611809", davidson.getDisease().getDiseaseId());
        assertEquals("BESTROPHINOPATHY, AUTOSOMAL RECESSIVE; ARB", davidson.getDisease().getDiseaseName());
        assertEquals("Subject 1", davidson.getFamilyInfo().getFamilyOrProbandId());
//        assertEquals("GRCh37", davidson.getGenomeBuild());
        assertEquals(6, davidson.getPhenotypeCount());

        assertEquals("Davidson AE, Sergouniotis PI, Burgess-Mullan R, Hart-Holden N, Low S, Foster PJ, Manson FD, " +
                "Black GC, Webster AR", davidson.getPublication().getAuthorList());
        assertEquals("Mol Vis", davidson.getPublication().getJournal());
        assertEquals("2916-22", davidson.getPublication().getPages());
        assertEquals("21203346", davidson.getPublication().getPmid());
        assertEquals("A synonymous codon variant in two patients with autosomal recessive bestrophinopathy alters in" +
                " vitro splicing of BEST1", davidson.getPublication().getTitle());
        assertEquals("16", davidson.getPublication().getVolume());
        assertEquals("2010", davidson.getPublication().getYear());

        assertEquals("BEST1", davidson.getGene().getSymbol());
        assertEquals(7439, davidson.getGene().getEntrezId());

        assertEquals(2, davidson.getVariantCount());
    }


    /**
     * Test correct deserialization of some attributes from Ars-2000-NF1-95-89.xml file.
     */
    @Test
    public void readSecondModel() throws Exception {
        DiseaseCase ars;
        try (InputStream is = getClass().getResourceAsStream("/models/xml/Ars-2000-NF1-95-89.xml")) {
            ars = parser.readModel(is).orElseThrow(() -> new Exception("Unable to decode the test data at /models/xml/Ars-2000-NF1-95-89.xml"));
        }

        assertEquals("HPO:lccarmody", ars.getBiocurator().getBiocuratorId());

        assertEquals("162200", ars.getDisease().getDiseaseId());
        assertEquals("NEUROFIBROMATOSIS, TYPE I; NF1", ars.getDisease().getDiseaseName());

        assertEquals("Mutations affecting mRNA splicing are the most common molecular defects in patients with neurofibromatosis type 1", ars.getPublication().getTitle());
        assertEquals("Ars E, Serra E, García J, Kruyer H, Gaona A, Lázaro C, Estivill X", ars.getPublication().getAuthorList());

        assertEquals(1, ars.getVariantCount());


        Variant v = ars.getVariant(0);
        assertEquals(GenomeAssembly.HG_19, v.getVariantPosition().getGenomeAssembly());
        assertEquals("7", v.getVariantPosition().getContig());
        assertEquals(10490, v.getVariantPosition().getPos());
        assertEquals("A", v.getVariantPosition().getRefAllele());
        assertEquals("CC", v.getVariantPosition().getAltAllele());
        assertFalse(v.getVariantValidation().getComparability());
        assertEquals("Exon skipping", v.getConsequence());
        assertTrue(v.getVariantValidation().getCosegregation());
        assertEquals(Genotype.HETEROZYGOUS, v.getGenotype());
        assertEquals("coding|missense", v.getPathomechanism());
        assertEquals("TATCTT[A/CC]AGGCTTTT", v.getSnippet());
        assertEquals("coding", v.getVariantClass());

        assertTrue(v.getVariantValidation().getRtPcrValidation());
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