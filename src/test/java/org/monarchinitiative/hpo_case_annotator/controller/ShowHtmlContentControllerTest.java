package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.TestApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * This test tests presenting content of {@link ShowHtmlContentController} to the user.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class ShowHtmlContentControllerTest extends ApplicationTest {

    @Autowired
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