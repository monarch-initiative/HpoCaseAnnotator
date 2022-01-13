package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled // unless specifically run
@ExtendWith(ApplicationExtension.class)
public class PhenotypicFeatureControllerTest {

    private final PhenotypicFeatureController controller = new PhenotypicFeatureController();

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PhenotypicFeatureController.class.getResource("PhenotypicFeature.fxml"));
        loader.setControllerFactory(clz -> controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test(FxRobot robot) {
        robot.sleep(25, TimeUnit.SECONDS);

    }

}