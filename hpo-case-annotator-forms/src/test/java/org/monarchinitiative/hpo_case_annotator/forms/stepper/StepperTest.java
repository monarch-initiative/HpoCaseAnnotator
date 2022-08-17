package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePublication;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class StepperTest extends BaseControllerTest {

    private Stepper<ObservableIndividualStudy> controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Stepper.class.getResource("Stepper.fxml"));

        loader.setControllerFactory(clz -> new Stepper<>());
        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Stepper test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void testEmpty(FxRobot robot) {
        robot.sleep(1, TimeUnit.SECONDS);

        IndividualStudySteps steps = prepareIndividualStudySteps();

        Consumer<Boolean> closeHook = status -> System.err.printf("Should we commit? %s%n", status);
        Platform.runLater(() -> controller.setConclude(closeHook));
        ObservableIndividualStudy study = ObservableIndividualStudy.defaultInstance();
        study.setPublication(new ObservablePublication());
        Platform.runLater(() -> controller.setData(study));
        Platform.runLater(() -> controller.setSteps(FXCollections.observableList(steps.getSteps())));

        robot.sleep(20, TimeUnit.SECONDS);

        ObservableIndividualStudy data = controller.getData();
        System.err.println(data);
    }

    @Test
    public void testIndividualSteps(FxRobot robot) {
        robot.sleep(1, TimeUnit.SECONDS);
        IndividualStudySteps steps = prepareIndividualStudySteps();

        Consumer<Boolean> closeHook = status -> System.err.printf("Should we commit? %s%n", status);
        Platform.runLater(() -> controller.setConclude(closeHook));
        Platform.runLater(() -> controller.setData(new ObservableIndividualStudy(TestData.V2.comprehensiveIndividualStudy())));
        Platform.runLater(() -> controller.setSteps(FXCollections.observableList(steps.getSteps())));

        robot.sleep(5, TimeUnit.MINUTES);

        System.err.println(controller.getData());
    }

    private static IndividualStudySteps prepareIndividualStudySteps() {
        SimpleObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(HPO);
        return new IndividualStudySteps(CONTROLLER_FACTORY, ontology);
    }
}