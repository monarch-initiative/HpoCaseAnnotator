package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.MockNamedEntityFinder;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

/**
 * Ensure the following line is added to VM options of the run configuration:
 * <pre>
 * -ea --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
 * </pre>
 */
@Disabled("GUI tests are run manually or not at all")
@ExtendWith(ApplicationExtension.class)
public class TextMiningTest {

    private static final MockNamedEntityFinder MOCK_ENTITY_FINDER = new MockNamedEntityFinder();
    private TextMining controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new TextMining();
        controller.hpoProperty().set(BaseControllerTest.HPO);
        controller.namedEntityFinderProperty().set(MOCK_ENTITY_FINDER);


        Scene scene = new Scene(controller);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showTheWidget(FxRobot robot) {
        robot.sleep(1, TimeUnit.SECONDS);

        robot.sleep(60, TimeUnit.SECONDS);

        controller.reviewedFeaturesProperty().get()
                .forEach(System.err::println);
    }

}