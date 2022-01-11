package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.test.ControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.util.Convert;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PhenotypeBrowserControllerTest extends BaseControllerTest {

    private PhenotypeBrowserController controller;

    @Start
    public void start(Stage stage) throws Exception {
        ControllerFactory factory = new ControllerFactory(GENOMIC_ASSEMBLY_REGISTRY, GENE_IDENTIFIER_SERVICE);
        FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeBrowser.fxml"));
        loader.setControllerFactory(factory::getController);

        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }


    @Test
    public void showIt(FxRobot robot) {
        Platform.runLater(() -> TestData.V2.comprehensiveFamilyStudy().phenotypicFeatures().stream()
                .map(Convert::toObservablePhenotypicFeature)
                .forEach(controller.phenotypeDescriptions()::add));
        Platform.runLater(() -> controller.setOntology(ONTOLOGY));

        robot.sleep(100, TimeUnit.SECONDS);
    }

}