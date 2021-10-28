package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Disabled // unless specifically run
@ExtendWith(ApplicationExtension.class)
public class OntologyTreeControllerTest {

    public static final File LOCAL_ONTOLOGY_OBO = new File("/home/ielis/tmp/hp.obo");

    private static Ontology ONTOLOGY = null;

    @BeforeAll
    public static void beforeAll() {
        ONTOLOGY = OntologyLoader.loadOntology(LOCAL_ONTOLOGY_OBO);
    }

    private OntologyTreeController controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new OntologyTreeController(ONTOLOGY);
        FXMLLoader loader = new FXMLLoader(OntologyTreeController.class.getResource("OntologyTree.fxml"));
        loader.setControllerFactory(clz -> controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test(FxRobot robot) {
        robot.sleep(20, TimeUnit.SECONDS);
    }

}