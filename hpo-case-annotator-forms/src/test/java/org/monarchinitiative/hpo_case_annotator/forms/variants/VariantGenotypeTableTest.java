package org.monarchinitiative.hpo_case_annotator.forms.variants;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class VariantGenotypeTableTest extends BaseControllerTest {

    private VariantGenotypeTable variantGenotype;

    @Start
    public void start(Stage stage) throws Exception {
        variantGenotype = new VariantGenotypeTable();

        Scene scene = new Scene(variantGenotype);
        stage.setScene(scene);
        stage.setTitle("Variant summary test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) {
        ObservableIndividualStudy study = new ObservableIndividualStudy(TestData.V2.comprehensiveIndividualStudy());

        System.err.println("Added genotypes");
        variantGenotype.dataProperty().bind(study.individualProperty());
        robot.sleep(3, TimeUnit.SECONDS);

        System.err.println("Adding variants");
        variantGenotype.variantsProperty().bind(study.variantsProperty());
        robot.sleep(3, TimeUnit.SECONDS);

        robot.sleep(10, TimeUnit.SECONDS);

        System.err.println(study);
    }
}