package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.TestData;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
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
        ObservableList<CuratedVariant> variants = FXCollections.observableArrayList();
//        variants.addListener(controller.curatedVariantChangeListener());

        variants.addAll(TestData.makeTestVariants().stream()
                .map(GenotypedVariant::curatedVariant)
                .toList());

//        Platform.runLater(() -> controller.presentComponent(TestData.makeIndividual()));

        robot.sleep(20, TimeUnit.SECONDS);

//        System.err.println(controller.getComponent());
    }
}