package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.forms.BaseControllerTest;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Ensure the following line is added to VM options of the run configuration:
 * <pre>
 * -ea --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
 * </pre>
 */
@Disabled("GUI tests are run manually or not at all")
@ExtendWith(ApplicationExtension.class)
public class TextMiningTest {

    // Paste this text into the payload text area.
    public static final String SOURCE_TEXT = """
        The patient was a 14-year-old boy presenting with abnormality of hands from 3 years of age without any family history. Six months before admission, he developed slender fingers, three bouts in total, without presence of arachnodactyly.
        Next-generation sequencing of his whole-exome was performed. The result of sequencing revealed a de novo heterozygous G-to-A nucleotide substitution at position 877 in exon 10 of the COL6A1 gene.
        After treatment, the hematuria healed, but the muscle weakness failed to improve.
        """;
    private static final MockNamedEntityFinder MOCK_ENTITY_FINDER = new MockNamedEntityFinder();
    private TextMining controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new TextMining();
        controller.hpoProperty().set(BaseControllerTest.HPO);
        controller.namedEntityFinderProperty().set(MOCK_ENTITY_FINDER);


        Scene scene = new Scene(controller);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showTheWidget(FxRobot robot) {
        robot.sleep(1, TimeUnit.SECONDS);
//        Platform.runLater(() -> controller.setResults(getExampleTestResults()));

        robot.sleep(60, TimeUnit.SECONDS);

        controller.reviewedFeaturesProperty().get()
                .forEach(System.err::println);
    }


    private record SimpleMinedTerm(TermId id, int start, int end, boolean isNegated) implements MinedTerm {}


    private static class MockNamedEntityFinder implements NamedEntityFinder {
        @Override
        public List<MinedTerm> process(String source) {
            return getExampleTestResults();
        }

        private static List<MinedTerm> getExampleTestResults() {
            // Based on text in https://pubmed.ncbi.nlm.nih.gov/30808312/, but updated to contain terms from HPO module.
            return List.of(
                    makeMinedTerm("HP:0002817", 50, 50+20, false), // Abnormality of the upper limb (abnormality of hands)
                    makeMinedTerm("HP:0001238", 161, 161+15, false), // Slender finger (slender fingers)
                    makeMinedTerm("HP:0001166", 220, 220+14, true) // Arachnodactyly (arachnodactyly)
            );
        }

        private static SimpleMinedTerm makeMinedTerm(String termId, int start, int end, boolean isNegated) {
            return new SimpleMinedTerm(TermId.of(termId), start, end, isNegated);
        }

    }
}