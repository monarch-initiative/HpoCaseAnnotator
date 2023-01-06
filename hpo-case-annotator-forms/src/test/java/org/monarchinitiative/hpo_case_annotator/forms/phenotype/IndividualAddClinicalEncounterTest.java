package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.MockNamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.model.v2.IndividualStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Ensure the following line is added to VM options of the run configuration:
 * <pre>
 * -ea --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
 * </pre>
 */
@Disabled("GUI tests are run manually or not at all")
@ExtendWith(ApplicationExtension.class)
public class IndividualAddClinicalEncounterTest {

    private static final MockNamedEntityFinder MOCK_ENTITY_FINDER = new MockNamedEntityFinder();
    private IndividualAddClinicalEncounter controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new IndividualAddClinicalEncounter();
        controller.hpoProperty().set(BaseControllerTest.HPO);
        controller.namedEntityFinderProperty().set(MOCK_ENTITY_FINDER);


        Scene scene = new Scene(controller);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showTheWidget(FxRobot robot) {
        IndividualStudy is = TestData.V2.comprehensiveIndividualStudy();
        ObservableIndividual individual = new ObservableIndividual(is.getIndividual());
        robot.sleep(1, TimeUnit.SECONDS);
        Platform.runLater(() -> controller.setData(individual));

        robot.sleep(60, TimeUnit.SECONDS);

        System.err.println(controller.dataProperty().get());
    }

}