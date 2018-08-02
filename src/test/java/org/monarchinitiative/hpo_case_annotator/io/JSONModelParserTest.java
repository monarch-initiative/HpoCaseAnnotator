package org.monarchinitiative.hpo_case_annotator.io;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.*;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Ignore("Not yet used")
public class JSONModelParserTest {

    private DiseaseCaseModel model;

    //    @Autowired
    private JSONModelParser parser;


    /**
     * This method programmatically creates complete model instance for testing. The model contains two variants: one
     * Splicing variant and one Mendelian variant with all fields initialized.
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private static DiseaseCaseModel prepareModel() {
        DiseaseCaseModel model = new DiseaseCaseModel();

        /* Genome build */
        model.setGenomeBuild("GRCh37");

        /* Publication data */
        model.setPublication(
                new Publication("van Bon BW, Balciuniene J, Fruhman G, Nagamani SC, Broome DL, " +
                        "Cameron E, Martinet D, Roulet E, Jacquemont S, Beckmann JS, Irons M, Potocki L, Lee B, Cheung SW, Patel A, Bellini M, Selicorni A, Ciccone R, Silengo M, Vetro A, Knoers NV, de Leeuw N, Pfundt R, Wolf B, Jira P, Aradhya S, Stankiewicz P, Brunner HG, Zuffardi O, Selleck SB, Lupski JR, de Vries BB",
                        "The phenotype of recurrent 10q22q23 deletions and duplications", "Eur J Hum Genet", "2011", "19" +
                        "(4)", "400-8", "21248748"));

        /* Metadata */
        model.setMetadata(new Metadata("Authors describe a consanguineous family where homozygous mutation caused " +
                "USH1D. Exon trapping was performed to show aberrant splicing. The mutation creates a novel acceptor " +
                "splice site motif resulting in an insertion of 7bp from 3'end of intron 45 in the cells transfected " +
                "with the mutant construct."));

        /* Gene under examination*/
        model.setTargetGene(new TargetGene("CDH23", "64072", "10"));

        /* Variants which belong to this model */
        SplicingVariant first = new SplicingVariant();
        first.setChromosome("10");
        first.setPosition("73550880");
        first.setReferenceAllele("G");
        first.setAlternateAllele("A");
        first.setSnippet("CGGCACC[G/A]GGTGCC");
        first.setGenotype("heterozygous");
        first.setVariantClass("splicing");
        first.setPathomechanism("splicing|3css|activated");
        first.setCosegregation("yes");
        first.setComparability("no");
        first.setConsequence("Alternative/cryptic 3' splice site");
        first.setCrypticPosition("73550881");
        first.setCrypticSpliceSiteType("3' splice site");
        first.setCrypticSpliceSiteSnippet("GCACCGG|GTGCCA");
        first.setSplicingValidation(new SplicingValidation(true, false, true, false, false, true, true,
                false, true));
        model.getVariants().add(first);

        MendelianVariant second = new MendelianVariant();
        second.setChromosome("10");
        second.setPosition("73550890");
        second.setReferenceAllele("G");
        second.setAlternateAllele("A");
        second.setSnippet("CGCCGCC[G/A]GGC");
        second.setGenotype("homozygous");
        second.setVariantClass("mendelian");
        second.setPathomechanism("RNA-gene|base-pairing|disrupted");
        second.setCosegregation("yes");
        second.setComparability("no");
        second.setRegulator("telomerase RNA component");
        second.setReporterRegulation("up");
        second.setReporterResidualActivity("123");
        second.setEmsaValidationPerformed("yes");
        second.setEmsaTFSymbol("ADDA");
        second.setEmsaGeneId("ABCDEF");
        second.setOther("up");
        second.setOtherEffect("something other");
        model.getVariants().add(second);

        /* Family/proband information */
        model.setFamilyInfo(new FamilyInfo("B", "male", "19"));

        /* HPO terms */
        model.getHpoList().addAll(Arrays.asList(
                new HPO("HP:0000365", "Hearing impairment", "YES"),
                new HPO("HP:0000510", "Rod-cone dystrophy", "YES"),
                new HPO("HP:0000822", "Hypertension", "NOT")));

        /* Disease-related information */
        model.setDisease(new Disease("OMIM", "601067", "USHER SYNDROME, TYPE ID; USH1DUSHER SYNDROME, TYPE ID/F, CDH23/PCDH15, DIGENIC, INCLUDED"));

        /* Biocurator */
        model.setBiocurator(new Biocurator("HPO:walterwhite"));

        return model;
    }


    private static String readFileContent(String filepath) throws Exception {
        Path fpath = Paths.get(filepath);
        return Files.lines(fpath).collect(Collectors.joining("\n"));
    }


    @Before
    public void setUp() throws Exception {
        model = prepareModel();
    }


    /**
     * Serialize model using JSONModelParser into StringWriter and compare content of the StringWriter with expected
     * content contained in JSON resource file
     */
    @Test
    @Ignore("Untested, because the feature is not being used at the moment")
    // TODO - make work when JSON serialization will be required
    public void saveModel() throws Exception {
        StringWriter writer = new StringWriter();
        boolean status = parser.saveModel(writer, model);
        assertTrue(status);
        String expected = readFileContent("target/test-classes/models/json/expected-test-model.json");
        assertEquals(expected, writer.toString());
    }


    @Test
    @Ignore("Not yet implemented")
    public void readModel() throws Exception {
        Optional<DiseaseCaseModel> modelOptional = parser.readModel("expected-test-model.json");
        assertTrue(modelOptional.isPresent());
        DiseaseCaseModel model = modelOptional.get();
        assertEquals(this.model, model);
    }

}