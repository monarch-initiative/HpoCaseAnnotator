package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled("GUI tests are run manually")
@ExtendWith(ApplicationExtension.class)
public class PedigreeControllerTest extends BaseControllerTest {

    private PedigreeController controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PedigreeController.class.getResource("Pedigree.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY);

        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        robot.sleep(20, TimeUnit.SECONDS);
    }
}