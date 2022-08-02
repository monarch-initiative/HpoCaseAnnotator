package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class MiningResultsVettingBoxTest {

    private MiningResultsVettingBox results;

    @Start
    public void start(Stage stage) {
        results = new MiningResultsVettingBox();

        Scene scene = new Scene(results);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void showTheWidget(FxRobot robot) {
        robot.sleep(5, TimeUnit.SECONDS);
        Platform.runLater(() -> results.setResults(getExampleTestResults()));

        robot.sleep(60, TimeUnit.SECONDS);

        System.err.println(results.getVettedPhenotypicFeatures());
    }

    private TextMiningResults getExampleTestResults() {
        // Based on text in https://pubmed.ncbi.nlm.nih.gov/30808312/, but updated to negate `Macroscopic hematuria`.
        String originalText = """
                The patient was a 14-year-old boy presenting with muscle weakness from 3 years of age without any family history. Six months before admission, he developed recurrent gross hematuria, three bouts in total, without presence of blood clots in the urine.
                Next-generation sequencing of his whole-exome was performed. The result of sequencing revealed a de novo heterozygous G-to-A nucleotide substitution at position 877 in exon 10 of the COL6A1 gene.
                After treatment, the hematuria healed, but the muscle weakness failed to improve.
                """;
        List<MinedTerm> minedTerms = List.of(
                makeMinedTerm("HP:0001324", 50, 50+15, false), // Muscle weakness
                makeMinedTerm("HP:0002907", 156, 156+25, false), // Hematuria (recurrent gross hematuria)
                makeMinedTerm("HP:0012587", 205, 205+44, true) // Macroscopic hematuria (without presence of blood clots in the urine)
                );
        return new SimpleTextMiningResults(originalText, minedTerms);
    }

    private static SimpleMinedTerm makeMinedTerm(String termId, int start, int end, boolean isNegated) {
        return new SimpleMinedTerm(TermId.of(termId), start, end, isNegated);
    }

    private record SimpleTextMiningResults(String sourceText, List<MinedTerm> minedTerms) implements TextMiningResults {
    }

    private record SimpleMinedTerm(TermId id, int start, int end, boolean isNegated) implements MinedTerm {
    }
}