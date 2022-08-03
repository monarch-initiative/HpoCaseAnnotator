package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MiningResultsVettingBox extends VBox {

    private final ObjectProperty<TextMiningResults> results = new SimpleObjectProperty<>();
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();

    @FXML
    private TextFlow textFlow;
    @FXML
    private ListView<VettedPhenotypicFeature> vettedPhenotypicFeatures;

    public MiningResultsVettingBox() {
        FXMLLoader loader = new FXMLLoader(MiningResultsVettingBox.class.getResource("MiningResults.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void initialize() {
        results.addListener((obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        });
    }

    private void bind(TextMiningResults results) {
        /* TODO - implement
        */
        String sourceText = results.sourceText();
        List<MinedTerm> sortedTerms = results.minedTerms().stream().sorted(Comparator.comparingInt(MinedTerm::start)).toList();
        List<Text> textList = new ArrayList<>();
        int start = 0;
        for (MinedTerm term : sortedTerms) {
            int termStart = term.start();
            int termEnd = term.end();
            if (start < termStart) {
                Text subText = new Text(sourceText.substring(start, termStart));
                textList.add(subText);
            }
            Text minedText = new Text(sourceText.substring(termStart, termEnd));
            minedText.setFill(Color.RED);
            textList.add(minedText);
            if (termEnd <= sourceText.length()) {
                start = termEnd;
            }
        }
        if (start < sourceText.length()) {
            Text endText = new Text(sourceText.substring(start));
            textList.add(endText);
        }
        textFlow.getChildren().addAll(textList);

    }

    private void unbind(TextMiningResults results) {
        /* TODO - implement
         */
    }

    public TextMiningResults getResults() {
        return results.get();
    }

    public ObjectProperty<TextMiningResults> resultsProperty() {
        return results;
    }

    public void setResults(TextMiningResults results) {
        this.results.set(results);
    }

    public Ontology getHpo() {
        return hpo.get();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public void setHpo(Ontology hpo) {
        this.hpo.set(hpo);
    }

    /**
     * Call the method when done with text mining result vetting to get the list of approved terms.
     *
     * @return the list of vetted phenotypic features.
     */
    public List<VettedPhenotypicFeature> getVettedPhenotypicFeatures() {
        return vettedPhenotypicFeatures.getItems();
    }
}
