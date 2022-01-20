package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Disabled("GUI tests are run manually")
@ExtendWith(ApplicationExtension.class)
public class IndividualVariantSummaryControllerTest extends BaseControllerTest {

    private IndividualVariantSummaryController controller;


    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(IndividualVariantSummaryController.class.getResource("IndividualVariantSummary.fxml"));
        loader.setControllerFactory(CONTROLLER_FACTORY);
        Parent parent = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test(FxRobot robot) {
        ObservableList<CuratedVariant> variants = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(controller.variants(), variants);
        ObservableMap<String, Genotype> genotypes = FXCollections.observableHashMap();
        Bindings.bindContentBidirectional(controller.genotypes(), genotypes);


        List<CuratedVariant> testVariants = TestData.V2.comprehensiveFamilyStudy().variants();
        variants.addAll(testVariants);
        for (CuratedVariant tv : testVariants) {
            genotypes.put(tv.md5Hex(), Genotype.UNKNOWN);
        }

        robot.sleep(10, TimeUnit.SECONDS);

        controller.genotypes().forEach((k, v) -> System.err.printf("%s: %s%n", k, v));
    }

}