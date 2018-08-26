package org.monarchinitiative.hpo_case_annotator.controllers;

import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.HpoCaseAnnotatorModule;
import org.testfx.framework.junit.ApplicationTest;

@Ignore("Gui tests are ignored for now")
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({HpoCaseAnnotatorModule.class})
public class ShowValidationResultsControllerTest extends ApplicationTest {

    @Test
    public void name() throws Exception {

    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}