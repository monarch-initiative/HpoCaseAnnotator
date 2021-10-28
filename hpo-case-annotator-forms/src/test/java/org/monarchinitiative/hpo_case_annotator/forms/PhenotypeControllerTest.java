package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.forms.test.ControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGenomicAssemblyRegistry;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class PhenotypeControllerTest {

    public static final File LOCAL_ONTOLOGY_OBO = new File("/home/ielis/tmp/hp.obo");

    @Start
    public void start(Stage stage) throws Exception {
        TestGenomicAssemblyRegistry genomicAssemblyRegistry = new TestGenomicAssemblyRegistry();
        TestGeneIdentifierService testGeneIdentifierService = null;
        Ontology ontology = OntologyLoader.loadOntology(LOCAL_ONTOLOGY_OBO);
        ControllerFactory factory = new ControllerFactory(genomicAssemblyRegistry, testGeneIdentifierService, ontology);
        FXMLLoader loader = new FXMLLoader(PhenotypeController.class.getResource("Phenotype.fxml"));
        loader.setControllerFactory(factory::getController);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void showIt(FxRobot robot) {
        robot.sleep(100, TimeUnit.SECONDS);
    }

}