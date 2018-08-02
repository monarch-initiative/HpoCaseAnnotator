package org.monarchinitiative.hpo_case_annotator.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.HpoCaseAnnotatorModule;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * This test tests presenting content of {@link ShowHtmlContentController} to the user.
 */
@Ignore("Gui tests are ignored for now")
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({HpoCaseAnnotatorModule.class})
public class ShowHtmlContentControllerTest extends ApplicationTest {

    @Inject
    private ShowHtmlContentController controller;


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


    @Test
    public void testSetContentURL() throws Exception {
        assertTrue(controller.setContent(getClass().getResource("/html/testHrmd.html")));
    }


    @Test
    public void testSetContentString() throws Exception {
        String htmlString = readFileContent("target/test-classes/html/testHrmd.html");
        assertTrue(controller.setContent(htmlString));
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestShowHtmlContentView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}