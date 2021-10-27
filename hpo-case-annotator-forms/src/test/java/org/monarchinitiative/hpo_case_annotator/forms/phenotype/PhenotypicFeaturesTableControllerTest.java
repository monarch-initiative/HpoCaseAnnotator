package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescriptionSimple;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.Period;
import java.util.concurrent.TimeUnit;

@Disabled // unless specifically run
@ExtendWith(ApplicationExtension.class)
public class PhenotypicFeaturesTableControllerTest {

    private final PhenotypicFeaturesTableController controller = new PhenotypicFeaturesTableController();

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(PhenotypicFeaturesTableController.class.getResource("PhenotypeFeatures.fxml"));
        loader.setControllerFactory(clz -> controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test(FxRobot robot) {
        robot.sleep(5, TimeUnit.SECONDS);
        controller.addFeatures(
                PhenotypeDescriptionSimple.of(TermId.of("HP:123456"), "ABC", Period.ZERO, Period.of(1, 2, 3), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:987654"), "DEF", Period.of(12, 0, 1), Period.of(12, 10, 3), false),
                PhenotypeDescriptionSimple.of(TermId.of("HP:456789"), "GHI", Period.of(15, 0, 1), Period.of(20, 10, 3), true),
                PhenotypeDescriptionSimple.of(TermId.of("HP:987654"), "JKL", Period.of(12, 0, 1), Period.of(12, 10, 3), false)
                );
        robot.sleep(20, TimeUnit.SECONDS);

    }

}