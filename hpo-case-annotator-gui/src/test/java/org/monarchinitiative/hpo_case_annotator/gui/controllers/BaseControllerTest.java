package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Injector;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public abstract class BaseControllerTest extends ApplicationTest {

    @Inject
    protected Injector injector;

    @BeforeClass
    public static void setUpBefore() throws Exception {
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

    public abstract void start(Stage stage) throws Exception;
}
