package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Injector;
import javafx.application.Platform;
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
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Note that the autocompletion attached to several gui elements here causes exceptions to be thrown if the robot's
 * input produces some autocompletion suggestions. Therefore, nonsense and non-medical characters are used as input.
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class DiseaseCaseDataControllerTest extends ApplicationTest {

    private static final String PUBMED_SUMMARY = "1: H MA, C CG, R PN. Classification. N Engl J Med. 2018 Oct 11;379(15):1452-1462. PubMed PMID: 30304648.";


    @Inject
    private Injector injector;

    private DiseaseCaseDataController controller;

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
        FXMLLoader loader = new FXMLLoader(DiseaseCaseDataController.class.getResource("DiseaseCaseDataView.fxml"));
//        loader.setResources(resourceBundle);
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @Test
    public void modelIsUnitializedAtTheBeginning() {
        assertThat(controller.isComplete(), is(false));
    }


    /**
     * This is all we test here for now. We need to settle down on a minimal requirements for the model to be valid.
     */
    @Test
    public void addMinimalRequiredInformation() {
        assertThat(controller.isComplete(), is(false));
        clickOn("#inputPubMedDataTextField")
                .write(PUBMED_SUMMARY)
                .clickOn("#inputPubMedDataButton")
                .type(KeyCode.ENTER);
        assertThat(controller.getData().getPublication(), is(Publication.newBuilder().setAuthorList("H MA, C CG, R PN")
                .setTitle("Classification").setJournal("N Engl J Med").setYear("2018")
                .setVolume("379(15)").setPages("1452-1462").setPmid("30304648").build()));
        assertThat(controller.isComplete(), is(false));

        // gene
        doubleClickOn("#entrezIDTextField")
                .write("3172").clickOn("#geneSymbolTextField");
        assertThat(controller.getData().getGene(), is(Gene.newBuilder().setEntrezId(3172).setSymbol("HNF4A").build()));
        assertThat(controller.isComplete(), is(false));

        // disease
        clickOn("#diseaseDatabaseComboBox")
                .moveBy(0, 25)
                .clickOn(MouseButton.PRIMARY)
                .clickOn("#diseaseIDTextField")
                .write("227645")
                .type(KeyCode.ENTER);
        assertThat(controller.getData().getDisease(), is(Disease.newBuilder().setDatabase("OMIM").setDiseaseId("227645").setDiseaseName("FANCONI ANEMIA, COMPLEMENTATION GROUP C; FANCC").build()));
        assertThat(controller.isComplete(), is(false));

        // family info
        doubleClickOn("#probandFamilyTextField")
                .write("PID")
                .clickOn("#sexComboBox")
                .moveBy(0, 45)
                .clickOn(MouseButton.PRIMARY)
                .clickOn("#ageTextField")
                .write("26Y");
        assertThat(controller.getData().getFamilyInfo(), is(FamilyInfo.newBuilder().setFamilyOrProbandId("PID").setAge("26Y").setSex(Sex.MALE).build()));

        //metadata
        clickOn("#metadataTextArea")
                .write("Meta");
        assertThat(controller.getData().getMetadata(), is("Meta"));
    }

    /**
     * There was a bug where the phenotypes were added twice. Absence of the bug is tested here.
     */
    @Test
    public void noDuplicateTermsPhenotypeTermsArePresent() {
        DiseaseCase data = DiseaseCase.newBuilder()
                .addAllPhenotype(Arrays.asList(
                        OntologyClass.newBuilder().setId("HP:0000822").setLabel("Hypertension").setNotObserved(false).build(),
                        OntologyClass.newBuilder().setId("HP:0000822").setLabel("Hypertension").setNotObserved(false).build(), // duplicate that should be removed
                        OntologyClass.newBuilder().setId("HP:0002615").setLabel("Hypotension").setNotObserved(true).build(),
                        OntologyClass.newBuilder().setId("HP:0002615").setLabel("Hypotension").setNotObserved(true).build(),
                        OntologyClass.newBuilder().setId("HP:0005185").setLabel("Global systolic dysfunction").setNotObserved(false).build()
                ))
                .build();
        Platform.runLater(() -> controller.presentData(data));
        sleep(50);

        final DiseaseCase received = controller.getData();
        final List<OntologyClass> phenotypes = received.getPhenotypeList();
        assertThat(phenotypes.size(), is(3));
        assertThat(phenotypes, hasItems(OntologyClass.newBuilder().setId("HP:0000822").setLabel("Hypertension").setNotObserved(false).build(),
                OntologyClass.newBuilder().setId("HP:0002615").setLabel("Hypotension").setNotObserved(true).build(),
                OntologyClass.newBuilder().setId("HP:0005185").setLabel("Global systolic dysfunction").setNotObserved(false).build()));
    }

    //    TODO - complete tests, improve coverage

}