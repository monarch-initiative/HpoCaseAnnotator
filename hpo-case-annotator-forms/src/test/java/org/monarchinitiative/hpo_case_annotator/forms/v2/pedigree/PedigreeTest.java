package org.monarchinitiative.hpo_case_annotator.forms.v2.pedigree;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.pedigree.Pedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PedigreeTest extends BaseControllerTest {

    private Pedigree pedigree;

    @Start
    public void start(Stage stage) throws Exception {
        pedigree = new Pedigree();

        Scene scene = new Scene(pedigree, 1200, 900);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableFamilyStudy study = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());

        Platform.runLater(() -> pedigree.setData(study.getPedigree()));

        robot.sleep(30, TimeUnit.SECONDS);

        System.err.println(pedigree.getData());
    }


}