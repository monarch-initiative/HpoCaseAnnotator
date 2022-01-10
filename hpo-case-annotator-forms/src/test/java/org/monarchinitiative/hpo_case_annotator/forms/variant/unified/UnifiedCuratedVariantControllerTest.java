package org.monarchinitiative.hpo_case_annotator.forms.variant.unified;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class UnifiedCuratedVariantControllerTest extends BaseControllerTest {

    private UnifiedCuratedVariantController controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(UnifiedCuratedVariantController.class.getResource("CuratedVariant.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY);
        Parent parent = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showIt(FxRobot robot) {
        robot.sleep(20, TimeUnit.SECONDS);
    }
}