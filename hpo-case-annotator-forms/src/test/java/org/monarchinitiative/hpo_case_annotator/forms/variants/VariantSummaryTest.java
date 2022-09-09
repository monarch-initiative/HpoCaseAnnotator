package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class VariantSummaryTest extends BaseControllerTest {

    private VariantSummary variantSummary;

    @Start
    public void start(Stage stage) throws Exception {
        variantSummary = new VariantSummary();
        variantSummary.functionalAnnotationRegistryProperty().set(FUNCTIONAL_ANNOTATION_REGISTRY);
        variantSummary.genomicAssemblyRegistryProperty().set(GENOMIC_ASSEMBLY_REGISTRY);

        Scene scene = new Scene(variantSummary);
        stage.setScene(scene);
        stage.setTitle("Variant summary test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        ObservableFamilyStudy familyStudy = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());


        robot.sleep(300, TimeUnit.MILLISECONDS);
        variantSummary.variantsProperty().bindBidirectional(familyStudy.variantsProperty());

        robot.sleep(5, TimeUnit.MINUTES);

        variantSummary.variantsProperty().get()
                .forEach(System.err::println);
        System.err.println("--------------------------------------------------------------------------------");
        familyStudy.getVariants()
                .forEach(System.err::println);
    }
}