package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Note that the autocompletion attached to several gui elements here causes exceptions to be thrown if the robot's
 * input produces some autocompletion suggestions. Therefore, nonsense and non-medical characters are used as input.
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class DataControllerTest extends ApplicationTest {

    private static final String PUBMED_SUMMARY = "1: Haendel MA, Chute CG, Robinson PN. Classification, Ontology, and Precision " +
            "Medicine. N Engl J Med. 2018 Oct 11;379(15):1452-1462. doi: " +
            "10.1056/NEJMra1615014. Review. PubMed PMID: 30304648.";


    @Inject
    private Injector injector;

    private DataController controller;

    private DiseaseCase model;


    @BeforeClass
    public static void setUp() throws Exception {
        // for headless GUI testing, set the "not.headless" system property to true or comment out if you want to see the
        // robot in action
        if (!Boolean.getBoolean("not.headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1200x760-32");
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(DataController.class.getResource("DataView.fxml"));
//        loader.setResources(resourceBundle);
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @Test
    public void modelIsUnitializedAtTheBeginning() {
        assertThat(controller.diseaseCaseIsComplete().get(), is(false));
        assertThat(controller.getCase(), is(DiseaseCase.getDefaultInstance()));
    }


    /**
     * This is all we test here for now. We need to settle down on a minimal requirements for the model to be valid.
     */
    @Test
    public void addMinimalRequiredInformation() {
        assertThat(controller.diseaseCaseIsComplete().get(), is(false));
        clickOn("#genomeBuildComboBox")
                .moveBy(0, 25)
                .clickOn(MouseButton.PRIMARY) // add hg19 forAllValidations
                .clickOn("#inputPubMedDataTextField")
                .write(PUBMED_SUMMARY)
                .clickOn("#inputPubMedDataButton")
                .type(KeyCode.ENTER)
                .clickOn("#entrezIDTextField")
                .write("3172");

        assertThat(controller.diseaseCaseIsComplete().get(), is(true));
        final DiseaseCase aCase = controller.getCase();

        assertThat(aCase.getGenomeBuild(), is(""));
        assertThat(aCase.getGene(), is(Gene.newBuilder().setEntrezId(3172).setSymbol("HNF4A").build()));
        assertThat(aCase.getPublication(), is(Publication.newBuilder()
                .setAuthorList("Haendel MA, Chute CG, Robinson PN")
                .setTitle("Classification, Ontology, and Precision Medicine")
                .setJournal("N Engl J Med")
                .setYear("2018")
                .setVolume("379(15)")
                .setPages("1452-1462")
                .setPmid("30304648")
                .build()));
    }

//    TODO - complete tests
/*
    @Ignore
    @Test
    public void testGenomeBox() throws Exception {
//        controller.setModel(model);
        // Genome forAllValidations ComboBox
        ComboBox<String> genome = lookup("#genomeBuildComboBox").query();
        genome.getItems().clear();  // clear values that are added from choice-basket.yml file to not depend on them.
        genome.getItems().add(PESTO);
        assertEquals(null, model.getGenomeBuild());
        clickOn(genome).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(PESTO, model.getGenomeBuild());
    }

    @Ignore
    @Test
    public void entrezID() throws Exception {
//        controller.setModel(model);
        // entrez id textField
        TextField entrez = lookup("#entrezIDTextField").query();
        assertEquals(null, model.getTargetGene().getEntrezID());
        doubleClickOn(entrez).write(SIMBA);
        assertEquals(SIMBA, model.getTargetGene().getEntrezID());
    }

    @Ignore
    @Test
    public void geneSymbol() throws Exception {
//        controller.setModel(model);
        // gene symbol textField
        TextField geneSymbol = lookup("#geneSymbolTextField").query();
        assertEquals(null, model.getTargetGene().getGeneName());
        doubleClickOn(geneSymbol).write(SIMBA);
        assertEquals(SIMBA, model.getTargetGene().getGeneName());
    }

    @Ignore
    @Test
    public void diseaseDB() throws Exception {
//        controller.setModel(model);

        // disease database
        ComboBox<String> database = lookup("#diseaseDatabaseComboBox").query();
        database.getItems().clear(); // clear values that are added from choice-basket.yml file to not depend on them.
        database.getItems().add(SIMBA);
        assertEquals(null, model.getDisease().getDatabase());
        clickOn(database).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(SIMBA, model.getDisease().getDatabase());
    }

    @Ignore
    @Test
    public void diseaseName() throws Exception {
//        controller.setModel(model);
        // disease name textField
        TextField diseaseName = lookup("#diseaseNameTextField").query();
        assertEquals(null, model.getDisease().getDiseaseName());
        doubleClickOn(diseaseName).write("()*&");
        assertEquals("()*&", model.getDisease().getDiseaseName());
    }

    @Ignore
    @Test
    public void diseaseID() throws Exception {
//        controller.setModel(model);
        // disease id textField
        TextField diseaseID = lookup("#diseaseIDTextField").query();
        assertEquals(null, model.getDisease().getDiseaseId());
        doubleClickOn(diseaseID).write(SIMBA);
        assertEquals(SIMBA, model.getDisease().getDiseaseId());
    }

    @Ignore
    @Test
    public void probandID() throws Exception {
//        controller.setModel(model);
        // proband family ID textField
        TextField probFam = lookup("#probandFamilyTextField").query();
        assertEquals(null, model.getFamilyInfo().getFamilyOrPatientID());
        doubleClickOn(probFam).write(PESTO);
        assertEquals(PESTO, model.getFamilyInfo().getFamilyOrPatientID());
    }

    @Ignore
    @Test
    public void sex() throws Exception {
//        controller.setModel(model);
        // sex combobox
        ComboBox<String> sex = lookup("#sexComboBox").query();
        sex.getItems().clear(); // clear values that are added from choice-basket.yml file to not depend on them.
        sex.getItems().add(SIMBA);
        assertNull(model.getFamilyInfo().getSex());
        clickOn(sex).moveBy(0, 35).press(MouseButton.PRIMARY);
        assertEquals(SIMBA, model.getFamilyInfo().getSex());
    }

    @Ignore
    @Test
    public void age() throws Exception {
//        controller.setModel(model);

        // age textField
        TextField age = lookup("#ageTextField").query();
        assertNull(model.getFamilyInfo().getAge());
        doubleClickOn(age).write("23");
        assertEquals("23", model.getFamilyInfo().getAge());
    }

    @Ignore
    @Test
    public void bioc() throws Exception {
//        controller.setModel(model);
        // biocurator textField - NOT EDITABLE
        TextField bioc = lookup("#biocuratorIdTextField").query();
        assertEquals(null, model.getBiocurator().getBioCuratorId());
        doubleClickOn(bioc).write(SIMBA);
        assertEquals(null, model.getBiocurator().getBioCuratorId()); // NOT - EDITABLE
    }

    @Ignore
    @Test
    public void metadata() throws Exception {
//        controller.setModel(model);
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
     *
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

    @Ignore
    @Test
    public void inputPubMedDataButton() throws Exception {
        DiseaseCaseModel model = new DiseaseCaseModel();
//        controller.setModel(model);
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
    */
}