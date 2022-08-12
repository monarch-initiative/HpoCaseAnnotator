package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class SelectablePhenotypeFeatureTreeViewTest extends BaseControllerTest {

    private SelectablePhenotypeFeatureTreeView treeView;

    @Start
    public void start(Stage stage) throws Exception {
        treeView = new SelectablePhenotypeFeatureTreeView();

        Scene scene = new Scene(treeView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Onto tree view test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }


    @Test
    public void testOntoTreeView(FxRobot robot) {
        robot.sleep(1, TimeUnit.SECONDS);

        treeView.addListener(obs -> System.err.println("Something changed"));
        Platform.runLater(() -> treeView.setOntology(HPO));

        robot.sleep(20, TimeUnit.SECONDS);

        treeView.ontoItemsProperty().forEach((k, v) -> System.err.printf("%s[%s]->%s%n", v.getName(), k.getValue(), v.getSelectionStatus()));
    }
}