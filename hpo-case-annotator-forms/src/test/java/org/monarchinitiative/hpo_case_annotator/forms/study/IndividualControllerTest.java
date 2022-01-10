package org.monarchinitiative.hpo_case_annotator.forms.study;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.TestData;
import org.monarchinitiative.hpo_case_annotator.forms.study.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled("GUI tests are run manually")
@ExtendWith(ApplicationExtension.class)
public class IndividualControllerTest {

    private final IndividualController controller = new IndividualController();
    private final IndividualVariantSummaryController variantSummaryController = new IndividualVariantSummaryController();

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(IndividualController.class.getResource("Individual.fxml"));
        loader.setControllerFactory(controllerFactory());

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    private Callback<Class<?>, Object> controllerFactory() {
        return clz -> {
            if (clz == IndividualController.class) {
                return controller;
            } else if (clz == IndividualVariantSummaryController.class) {
                return variantSummaryController;
            } else return null;
        };
    }


    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableList<CuratedVariant> variants = FXCollections.observableArrayList();
        variants.addListener(controller.curatedVariantChangeListener());

        variants.addAll(TestData.makeTestVariants().stream()
                .map(GenotypedVariant::curatedVariant)
                .toList());

        Platform.runLater(() -> {
            Individual individual = TestData.makeIndividual();
            BaseObservableIndividual observableIndividual = Convert.toObservableIndividual(individual);
            controller.setIndividual(observableIndividual);
        });



        robot.sleep(10, TimeUnit.SECONDS);

    }

}