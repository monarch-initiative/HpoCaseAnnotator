package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules(TestHpoCaseAnnotatorModule.class)
@Ignore // only use for development
public class LiftoverControllerTest extends ApplicationTest {

    @Inject
    private LiftoverController instance;

    @Before
    public void setUp() throws Exception {
        System.setProperty("headless", "false"); // set this to false if you want to see the robot in action.
        System.setProperty("testfx.headless", "true"); // REQUIRED
        System.setProperty("testfx.robot", "glass"); // REQUIRED
        System.setProperty("prism.order", "sw"); // REQUIRED
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("headless");
        System.clearProperty("testfx.robot");
        System.clearProperty("testfx.headless");
        System.clearProperty("prism.order");
    }

    @Test
    public void display() {
        sleep(10, TimeUnit.SECONDS);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(LiftoverController.class.getResource("LiftoverView.fxml"));
        loader.setControllerFactory(param -> instance);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}