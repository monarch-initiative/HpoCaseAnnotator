package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.TestApplicationConfig;
import org.monarchinitiative.hpo_case_annotator.gui.application.HRMDResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

/**
 * This test tests presenting content of {@link HRMDResources} to the user.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class ShowResourcesControllerTest extends ApplicationTest {

    @Autowired
    private ShowResourcesController controller;


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
     * At the moment, there are 6 resources/properties in {@link HRMDResources}. That's all that is tested here.
     */
    @Test
    public void testInitialization() throws Exception {
        assertEquals(6, controller.getContentTableView().getItems().size());
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TestShowResourcesView.fxml"));
        loader.setControllerFactory(param -> controller);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}