package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

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
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PhenotypeEntryControllerTest extends BaseControllerTest {

    private PhenotypeEntryController controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeEntry.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY::getController);
        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showIt(FxRobot robot) {
        Platform.runLater(() -> controller.setOntology(ONTOLOGY));
        Platform.runLater(() -> controller.onsetAge().set(new ObservableAge(5, 2, 1)));
        Platform.runLater(() -> controller.resolutionAge().set(new ObservableAge(10, 8, 31)));

        robot.sleep(100, TimeUnit.SECONDS);
    }
}