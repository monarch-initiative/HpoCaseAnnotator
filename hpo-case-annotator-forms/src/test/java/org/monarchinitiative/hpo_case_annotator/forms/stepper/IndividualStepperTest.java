package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class IndividualStepperTest extends BaseControllerTest {

    private Stepper<ObservableIndividualStudy> stepper;

    @Start
    public void start(Stage stage) throws Exception {
        stepper = new Stepper<>();
        stepper.statusProperty().addListener((obs, old, novel) -> {
            stage.close();
            System.err.println(stepper.getData());
        });

        Scene scene = new Scene(stepper, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Stepper test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void testEmpty(FxRobot robot) {
        robot.sleep(300, TimeUnit.MILLISECONDS);

        IndividualStudySteps steps = new IndividualStudySteps().configureSteps();
        wireFunctionalPropertiesToStudyResourcesAware(steps);

        ObservableIndividualStudy study = new ObservableIndividualStudy();
        Platform.runLater(() -> stepper.setData(study));
        Platform.runLater(() -> stepper.stepsProperty().bind(steps.stepsProperty()));

        robot.sleep(5, TimeUnit.MINUTES);
    }

    @Test
    public void testIndividualSteps(FxRobot robot) {
        robot.sleep(300, TimeUnit.MILLISECONDS);
        IndividualStudySteps steps = new IndividualStudySteps().configureSteps();
        wireFunctionalPropertiesToStudyResourcesAware(steps);

        Platform.runLater(() -> stepper.setData(new ObservableIndividualStudy(TestData.V2.comprehensiveIndividualStudy())));
        Platform.runLater(() -> stepper.stepsProperty().bind(steps.stepsProperty()));

        robot.sleep(5, TimeUnit.MINUTES);
    }

    private void wireFunctionalPropertiesToStudyResourcesAware(StudyResourcesAware resourcesAware) {
        StudyResources resources = resourcesAware.getStudyResources();
        resources.setHpo(HPO);
        resources.setDiseaseIdentifierService(DISEASES);
        resources.setGenomicAssemblyRegistry(GENOMIC_ASSEMBLY_REGISTRY);
        resources.setFunctionalAnnotationRegistry(FUNCTIONAL_ANNOTATION_REGISTRY);
    }

}