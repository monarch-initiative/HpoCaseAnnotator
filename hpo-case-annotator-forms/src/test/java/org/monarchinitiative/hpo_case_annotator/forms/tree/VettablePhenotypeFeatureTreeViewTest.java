package org.monarchinitiative.hpo_case_annotator.forms.tree;

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
public class VettablePhenotypeFeatureTreeViewTest extends BaseControllerTest {

    private VettableOntologyClassTreeView treeView;

    @Start
    public void start(Stage stage) throws Exception {
        treeView = new VettableOntologyClassTreeView();

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

        treeView.ontoItemsProperty().forEach((k, v) -> System.err.printf("%s[%s]->%s%n", v.getLabel(), k.getValue(), v.getSelectionStatus()));
    }
}