package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.TestApplicationConfig;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.Publication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

/**
 * Note that the autocompletion attached to several gui elements here causes exceptions to be thrown if the robot's
 * input produces some autocompletion suggestions. Therefore, nonsense and non-medical characters are used as input.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
@Ignore
public class DataControllerTest extends ApplicationTest {

    private static final String PUBMED = "1: van Bon BW, Balciuniene J, Fruhman G, Nagamani SC, Broome DL, Cameron" +
            " E, Martinet D, Roulet E, Jacquemont S, Beckmann JS, Irons M, Potocki L, Lee B, Cheung SW, Patel A, " +
            "Bellini M, Selicorni A, Ciccone R, Silengo M, Vetro A, Knoers NV, de Leeuw N, Pfundt R, Wolf B, Jira P, " +
            "Aradhya S, Stankiewicz P, Brunner HG, Zuffardi O, Selleck SB, Lupski JR, de Vries BB. The phenotype of " +
            "recurrent 10q22q23 deletions and duplications. Eur J Hum Genet. 2011 Apr;19(4):400-8. doi: " +
            "10.1038/ejhg.2010.211. Epub 2011 Jan 19. PubMed PMID: 21248748; PubMed Central PMCID: PMC3060324.";

    /**
     * Dummy strings with values which will be written into tested gui elements.
     */
    private static String PESTO, SIMBA;

    @Autowired
    private DataController controller;

    private DiseaseCaseModel model;


    @BeforeClass
    public static void setUp() throws Exception {
        PESTO = "PESTO";
        SIMBA = "SAMBA";
//        if (Boolean.getBoolean("headless")) {
        System.setProperty("headless", "true"); // set this to false if you want to see the robot in action.
        System.setProperty("testfx.headless", "true"); // REQUIRED
        System.setProperty("testfx.robot", "glass"); // REQUIRED
        System.setProperty("prism.order", "sw"); // REQUIRED
//        }

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
//        if (Boolean.getBoolean("headless")) {
        System.clearProperty("headless");
        System.clearProperty("testfx.robot");
        System.clearProperty("testfx.headless");
        System.clearProperty("prism.order");
//        }
    }


    @Test
    public void testGenomeBox() throws Exception {
        controller.setModel(model);
        // Genome build ComboBox
        ComboBox<String> genome = lookup("#genomeBuildComboBox").query();
        genome.getItems().clear();  // clear values that are added from templateParameters.yml file to not depend on them.
        genome.getItems().add(PESTO);
        assertEquals(null, model.getGenomeBuild());
        clickOn(genome).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(PESTO, model.getGenomeBuild());
    }


    @Test
    public void entrezID() throws Exception {
        controller.setModel(model);
        // entrez id textField
        TextField entrez = lookup("#entrezIDTextField").query();
        assertEquals(null, model.getTargetGene().getEntrezID());
        doubleClickOn(entrez).write(SIMBA);
        assertEquals(SIMBA, model.getTargetGene().getEntrezID());
    }


    @Test
    public void geneSymbol() throws Exception {
        controller.setModel(model);
        // gene symbol textField
        TextField geneSymbol = lookup("#geneSymbolTextField").query();
        assertEquals(null, model.getTargetGene().getGeneName());
        doubleClickOn(geneSymbol).write(SIMBA);
        assertEquals(SIMBA, model.getTargetGene().getGeneName());
    }


    @Test
    public void diseaseDB() throws Exception {
        controller.setModel(model);

        // disease database
        ComboBox<String> database = lookup("#diseaseDatabaseComboBox").query();
        database.getItems().clear(); // clear values that are added from templateParameters.yml file to not depend on them.
        database.getItems().add(SIMBA);
        assertEquals(null, model.getDisease().getDatabase());
        clickOn(database).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(SIMBA, model.getDisease().getDatabase());
    }


    @Test
    public void diseaseName() throws Exception {
        controller.setModel(model);
        // disease name textField
        TextField diseaseName = lookup("#diseaseNameTextField").query();
        assertEquals(null, model.getDisease().getDiseaseName());
        doubleClickOn(diseaseName).write("()*&");
        assertEquals("()*&", model.getDisease().getDiseaseName());
    }


    @Test
    public void diseaseID() throws Exception {
        controller.setModel(model);
        // disease id textField
        TextField diseaseID = lookup("#diseaseIDTextField").query();
        assertEquals(null, model.getDisease().getDiseaseId());
        doubleClickOn(diseaseID).write(SIMBA);
        assertEquals(SIMBA, model.getDisease().getDiseaseId());
    }


    @Test
    public void probandID() throws Exception {
        controller.setModel(model);
        // proband family ID textField
        TextField probFam = lookup("#probandFamilyTextField").query();
        assertEquals(null, model.getFamilyInfo().getFamilyOrPatientID());
        doubleClickOn(probFam).write(PESTO);
        assertEquals(PESTO, model.getFamilyInfo().getFamilyOrPatientID());
    }


    @Test
    public void sex() throws Exception {
        controller.setModel(model);
        // sex combobox
        ComboBox<String> sex = lookup("#sexComboBox").query();
        sex.getItems().clear(); // clear values that are added from templateParameters.yml file to not depend on them.
        sex.getItems().add(SIMBA);
        assertEquals(null, model.getFamilyInfo().getSex());
        clickOn(sex).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(SIMBA, model.getFamilyInfo().getSex());
    }


    @Test
    public void age() throws Exception {
        controller.setModel(model);

        // age textField
        TextField age = lookup("#ageTextField").query();
        assertEquals(null, model.getFamilyInfo().getAge());
        doubleClickOn(age).write("23");
        assertEquals("23", model.getFamilyInfo().getAge());
    }


    @Test
    public void bioc() throws Exception {
        controller.setModel(model);
        // biocurator textField - NOT EDITABLE
        TextField bioc = lookup("#biocuratorIdTextField").query();
        assertEquals(null, model.getBiocurator().getBioCuratorId());
        doubleClickOn(bioc).write(SIMBA);
        assertEquals(null, model.getBiocurator().getBioCuratorId()); // NOT - EDITABLE
    }


    @Test
    public void metadata() throws Exception {
        controller.setModel(model);
        // metadata textField - NOT EDITABLE
        TextArea metada = lookup("#metadataTextArea").query();
        assertEquals(null, model.getMetadata().getMetadataText());
        doubleClickOn(metada).write(PESTO);
        assertEquals(PESTO, model.getMetadata().getMetadataText());
    }


    /**
     * This test sets an empty {@link DiseaseCaseModel} and by sequential adding of content into gui elements tests the
     * bindings between model & view properties.
     *
     * @throws Exception
     */

    @Test
    @Ignore
    public void addHpoTermButtonAction() throws Exception {
    }


    @Test
    @Ignore
    public void addVariantButtonAction() throws Exception {
    }


    @Test
    @Ignore
    public void hpoTextMiningButtonAction() throws Exception {
    }


    @Test
    public void inputPubMedDataButton() throws Exception {
        DiseaseCaseModel model = new DiseaseCaseModel();
        controller.setModel(model);
        TextField pubMed = lookup("#inputPubMedDataTextField").query();
        Button pubMedButton = lookup("#inputPubMedButton").query();
        pubMed.setText(PUBMED);
        clickOn(pubMedButton);
        Publication publication = model.getPublication();
        assertEquals("van Bon BW, Balciuniene J, Fruhman G, Nagamani SC, Broome DL, Cameron E, Martinet D, Roulet E, Jacquemont S, Beckmann JS, Irons M, Potocki L, Lee B, Cheung SW, Patel A, Bellini M, Selicorni A, Ciccone R, Silengo M, Vetro A, Knoers NV, de Leeuw N, Pfundt R, Wolf B, Jira P, Aradhya S, Stankiewicz P, Brunner HG, Zuffardi O, Selleck SB, Lupski JR, de Vries BB", publication.getAuthorlist());
        assertEquals("van Bon", publication.getFirstAuthorSurname());
        assertEquals("The phenotype of recurrent 10q22q23 deletions and duplications", publication.getTitle());
        assertEquals("Eur J Hum Genet", publication.getJournal());
        assertEquals("2011", publication.getYear());
        assertEquals("19(4)", publication.getVolume());
        assertEquals("400-8", publication.getPages());
        assertEquals("21248748", publication.getPmid());
    }


    @Test
    @Ignore
    public void removeVariantAction() throws Exception {
    }


    @Test
    @Ignore
    public void showAllEditHpoTermsButtonAction() throws Exception {
    }


    /**
     * This test examines the initialization of view elements content.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void initialize() throws Exception {

    }


    @Override
    public void start(Stage stage) throws Exception {
        model = new DiseaseCaseModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestDataView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}