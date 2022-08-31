package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.application.Platform;
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
public class DiseaseDataEditTest extends BaseControllerTest {


    private PedigreeMemberDiseaseDataEdit diseaseDataEdit;

    @Start
    public void start(Stage stage) throws Exception {
        diseaseDataEdit = new PedigreeMemberDiseaseDataEdit();
        diseaseDataEdit.setDiseaseIdentifierService(DISEASES);

        Scene scene = new Scene(diseaseDataEdit);
        stage.setScene(scene);
        stage.setTitle("Disease view test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableFamilyStudy familyStudy = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());

        ObservablePedigreeMember member = familyStudy.getPedigree().membersProperty().get(0);
        Platform.runLater(() -> diseaseDataEdit.setInitialData(member));


        member.getDiseaseStates().forEach(System.err::println);
        robot.sleep(5, TimeUnit.SECONDS);
        member.getDiseaseStates().forEach(System.err::println);
        Platform.runLater(() -> diseaseDataEdit.commit());
        robot.sleep(2, TimeUnit.SECONDS);
        member.getDiseaseStates().forEach(System.err::println);
    }

}