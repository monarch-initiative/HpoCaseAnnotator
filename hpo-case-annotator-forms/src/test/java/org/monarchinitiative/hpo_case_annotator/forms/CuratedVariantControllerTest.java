package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.core.reference.GeneIdentifier;
import org.monarchinitiative.hpo_case_annotator.forms.test.ControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGeneIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.test.TestGenomicAssemblyRegistry;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class CuratedVariantControllerTest {


    @Start
    public void start(Stage stage) throws Exception {
        TestGenomicAssemblyRegistry genomicAssemblyRegistry = new TestGenomicAssemblyRegistry();
        TestGeneIdentifierService testGeneIdentifierService = getGeneIdService();
        ControllerFactory factory = new ControllerFactory(genomicAssemblyRegistry, testGeneIdentifierService);
        FXMLLoader loader = new FXMLLoader(CuratedVariantController.class.getResource("CuratedVariant.fxml"));
        loader.setControllerFactory(factory::getController);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    private TestGeneIdentifierService getGeneIdService() {
        Map<GeneIdentifier, List<String>> genes = Map.of(
                new GeneIdentifier(TermId.of("NCBIGene:1"), "HNF4A"), List.of("NM_000001.1", "NM_000002.1"),
                new GeneIdentifier(TermId.of("NCBIGene:2"), "FBN1"), List.of("NM_000003.1", "NM_000004.1"),
                new GeneIdentifier(TermId.of("NCBIGene:3"), "GCK"), List.of("NM_000005.1", "NM_000006.1")
        );
        return new TestGeneIdentifierService(genes);
    }

    @Test
    public void showIt(FxRobot robot) {
        robot.sleep(20, TimeUnit.SECONDS);
    }
}