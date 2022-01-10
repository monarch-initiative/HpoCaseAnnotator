package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled("GUI tests are run manually")
@ExtendWith(ApplicationExtension.class)
public class IndividualVariantSummaryControllerTest {

    private final IndividualVariantSummaryController controller = new IndividualVariantSummaryController();


    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(IndividualVariantSummaryController.class.getResource("VariantSummary.fxml"));
        loader.setControllerFactory(clz -> controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test(FxRobot robot) {
        Platform.runLater(() -> controller.genotypedVariants().addAll(TestData.makeTestVariants()));

        robot.sleep(10, TimeUnit.SECONDS);

        controller.genotypedVariants().forEach(System.err::println);
    }

}