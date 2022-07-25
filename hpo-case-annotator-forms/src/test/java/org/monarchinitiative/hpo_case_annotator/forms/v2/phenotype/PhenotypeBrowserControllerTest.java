package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PhenotypeBrowserControllerTest extends BaseControllerTest {

    private PhenotypeBrowserController<ObservablePedigreeMember> controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeBrowser.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY::getController);

        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }


    @Test
    public void showIt(FxRobot robot) {
        FamilyStudy study = TestData.V2.comprehensiveFamilyStudy();
        ObjectProperty<ObservablePedigreeMember> individual = new SimpleObjectProperty<>();
        if (study.getMembers().isEmpty())
            throw new IllegalArgumentException("Test cannot work without at least one individual");

        PedigreeMember member = study.getMembers().get(0);
        controller.getData().update(member);

        Platform.runLater(() -> controller.setOntology(ONTOLOGY));
        Platform.runLater(() -> controller.dataProperty().bind(individual));

        robot.sleep(20, TimeUnit.SECONDS);

        System.err.println(controller.getData());
    }

}