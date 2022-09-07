package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class IndividualStudyTest extends BaseControllerTest {

    private IndividualStudyComponent component;

    @Start
    public void start(Stage stage) throws Exception {
        component = new IndividualStudyComponent();
        component.hpoProperty().set(HPO);
        component.diseaseIdentifierServiceProperty().set(DISEASES);

        Scene scene = new Scene(component, 1200, 900);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Individual study test");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableIndividualStudy study = new ObservableIndividualStudy(TestData.V2.comprehensiveIndividualStudy());

        Platform.runLater(() -> this.component.setData(study));

        robot.sleep(1, TimeUnit.MINUTES);

        System.err.println(this.component.getData());
    }
}