package org.monarchinitiative.hpo_case_annotator.forms.v2.publication;

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
import org.monarchinitiative.hpo_case_annotator.forms.publication.Publication;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PublicationTest extends BaseControllerTest {

    private Publication controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Publication.class.getResource("Publication.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY);

        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent, 1200, 900);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableFamilyStudy familyStudy = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());

        Platform.runLater(() -> controller.setData(familyStudy.getPublication()));

        robot.sleep(10, TimeUnit.SECONDS);

        System.err.println(controller.getData());
    }


}