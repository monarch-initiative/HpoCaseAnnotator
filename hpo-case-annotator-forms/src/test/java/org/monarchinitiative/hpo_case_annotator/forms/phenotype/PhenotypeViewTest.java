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
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PhenotypeViewTest extends BaseControllerTest {

    private PhenotypeView controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PhenotypeView.class.getResource("PhenotypeView.fxml"));

        loader.setControllerFactory(CONTROLLER_FACTORY);
        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Phenotype view test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableFamilyStudy familyStudy = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());

        ObservablePedigreeMember member = familyStudy.getPedigree().membersProperty().get(0);
        Platform.runLater(() -> controller.setInitialData(member));

        robot.sleep(60, TimeUnit.SECONDS);

        System.err.println(member);
        Platform.runLater(() -> controller.commit());
        robot.sleep(1, TimeUnit.SECONDS);
        System.err.println(member);
    }

}