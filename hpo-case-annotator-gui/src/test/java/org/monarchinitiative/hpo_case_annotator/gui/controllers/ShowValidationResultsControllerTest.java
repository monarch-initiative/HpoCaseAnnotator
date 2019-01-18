package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.gui.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class ShowValidationResultsControllerTest extends ApplicationTest {

    @Inject
    private Injector injector;

    private ShowValidationResultsController controller;

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

    private static Map<DiseaseCase, List<ValidationResult>> makeMultipleCases() {
        Map<DiseaseCase, List<ValidationResult>> cases = new HashMap<>();
        // first
        DiseaseCase first = DiseaseCase.newBuilder()
                .setPublication(DiseaseCaseModelExample.makeExomiserPublication())
                .setGene(Gene.newBuilder().setEntrezId(2200).setSymbol("FBN1").build())
                .build();
        final List<ValidationResult> firstResults = Collections.singletonList(ValidationResult.fail("Missing something"));
        cases.put(first, firstResults);

        final DiseaseCase second = DiseaseCase.newBuilder()
                .setPublication(DiseaseCaseModelExample.makeJannovarPublication())
                .setGene(Gene.newBuilder().setEntrezId(3172).setSymbol("HNF4A").build())
                .build();
        final List<ValidationResult> secondResults = Collections.singletonList(ValidationResult.pass());
        cases.put(second, secondResults);

        return cases;
    }

    @Test
    public void displayValidationResultsForASingleCase() throws Exception {
        DiseaseCase simpleCase = DiseaseCase.newBuilder()
                .setPublication(DiseaseCaseModelExample.makeExomiserPublication())
                .setGene(Gene.newBuilder().setEntrezId(2200).setSymbol("FBN1").build())
                .build();
        List<ValidationResult> results = Arrays.asList(ValidationResult.fail("An error"), ValidationResult.pass());
        controller.setValidationResult(simpleCase, results);

        final TableView<ShowValidationResultsController.ValidationLine> validationLineTableView = lookup("#validationResultsTableView").query();
        final List<ShowValidationResultsController.ValidationLine> items = validationLineTableView.getItems();

        assertThat(items.size(), is(2));
        assertThat(items, hasItems(
                new ShowValidationResultsController.ValidationLine("Smedley-2015-FBN1", ValidationResult.fail("An error")),
                new ShowValidationResultsController.ValidationLine("Smedley-2015-FBN1", ValidationResult.pass())
        ));
    }

    @Test
    public void displayValidationResultsForMultipleCases() throws Exception {
        controller.setValidationResults(makeMultipleCases());

        final TableView<ShowValidationResultsController.ValidationLine> validationLineTableView = lookup("#validationResultsTableView").query();
        final List<ShowValidationResultsController.ValidationLine> items = validationLineTableView.getItems();

        assertThat(items.size(), is(2));
        assertThat(items, hasItems(
                new ShowValidationResultsController.ValidationLine("Smedley-2015-FBN1", ValidationResult.fail("Missing something")),
                new ShowValidationResultsController.ValidationLine("Jäger-2014-HNF4A", ValidationResult.pass())
        ));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ShowValidationResultsController.class.getResource("ShowValidationResults.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }
}