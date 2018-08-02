package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.HpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the GUI elements of the {@link SetResourcesController} controller. The most of the elements are not
 * editable and that's also what is being tested. The changes in GUI elements must result in changes in {@link
 * OptionalResources} properties to which the elements are bounded.
 */
@Ignore("Gui tests are ignored for now")
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({HpoCaseAnnotatorModule.class})
public class SetResourcesControllerTest extends ApplicationTest {

    @Inject
    private SetResourcesController controller;

    @Inject
    private OptionalResources resources;


    @BeforeClass
    public static void setUp() throws Exception {
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
    public void testInitialization() throws Exception {
        TextField dataDir = lookup("#dataDirectoryTextField").query();
        TextField refGen = lookup("#refGenomeTextField").query();
        TextField hp = lookup("#hpOBOTextField").query();
        TextField entrez = lookup("#entrezGeneFileTextField").query();
        TextField curDir = lookup("#curatedDirTextField").query();
        TextField biocur = lookup("#biocuratorIDTextField").query();
        assertEquals("target/test-classes", dataDir.getText());
        assertEquals("target/test-classes", refGen.getText());
        assertEquals("target/test-classes/HP.obo", hp.getText());
        assertEquals("target/test-classes/Homo_sapiens.gene_info.gz", entrez.getText());
        assertEquals("target/test-classes", curDir.getText());
        assertEquals("HPO:wwhite", biocur.getText());

    }


    @Test
    public void writeDataDir() throws Exception {
//        TextField field = lookup("#dataDirectoryTextField").query();
//        clickOn(field).write("tralala");
//        assertEquals("target/test-classes", field.getText());
//        assertEquals("target/test-classes", resources.getDataDir()); // the textfield is not editable.
    }


    @Test
    public void writeRefGen() throws Exception {
        TextField field = lookup("#refGenomeTextField").query();
        clickOn(field).write("tralala");
        assertEquals("target/test-classes", field.getText());
        assertEquals("target/test-classes", resources.getRefGenomeDir()); // the textfield is not editable.
    }


    @Test
    public void writeHPobo() throws Exception {
//        TextField field = lookup("#hpOBOTextField").query();
//        clickOn(field).write("tralala");
//        assertEquals("target/test-classes/HP.obo", field.getText());
//        assertEquals("target/test-classes/HP.obo", resources.getHpOBOPath()); // the textfield is not editable.
    }


    @Test
    public void writeEntrez() throws Exception {
//        TextField field = lookup("#entrezGeneFileTextField").query();
//        clickOn(field).write("tralala");
//        assertEquals("target/test-classes/Homo_sapiens.gene_info.gz", field.getText());
//        assertEquals("target/test-classes/Homo_sapiens.gene_info.gz", resources.getEntrezGenePath()); // the textfield is not editable.
    }


    @Test
    public void writeCurated() throws Exception {
        TextField field = lookup("#curatedDirTextField").query();
        clickOn(field).write("tralala");
        assertEquals("target/test-classes", field.getText());
        assertEquals("target/test-classes", resources.getDiseaseCaseDir()); // the textfield is not editable.
    }


    @Test
    public void writeHPO() throws Exception {
        TextField hpo = lookup("#biocuratorIDTextField").query();
        doubleClickOn(hpo).write("wwonka");
        assertEquals("HPO:wwonka", resources.getBiocuratorId());
        doubleClickOn(hpo).write("wwhite");
    }


    /**
     * This method is run before each test so it's similar than {@literal @}Before, but unlike before method this has a
     * parameter stage.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
//        resources = manager.getResources();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestSetResourcesView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

}