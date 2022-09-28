package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.*;

public class MiningResultsVetting extends VBox {

    private final StringProperty sourceText = new SimpleStringProperty();

    @FXML
    private TextFlow textFlow;

    public MiningResultsVetting() {
        FXMLLoader loader = new FXMLLoader(MiningResultsVetting.class.getResource("MiningResultsVetting.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty sourceTextProperty() {
        return sourceText;
    }

    ListChangeListener<? super ObservableReviewedPhenotypicFeature> updateTexts() {
        return change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved())
                    /* The change may be triggered by internal change of the list element too.
                    However, we only re-draw texts upon adding/removal of the list elements. */
                    textFlow.getChildren().setAll(prepareTexts(sourceText.get(), change.getList()));
            }
        };
    }

    private ObservableList<Text> prepareTexts(String sourceText, List<? extends ObservableReviewedPhenotypicFeature> sortedTerms) {
        int start = 0;

        List<Text> texts = new ArrayList<>();
        for (ObservableReviewedPhenotypicFeature term : sortedTerms) {
            int termStart = term.start();
            int termEnd = term.end();
            if (start < termStart) {
                Text plainText = new Text(sourceText.substring(start, termStart));
                texts.add(plainText);
            }

            String concept = sourceText.substring(termStart, termEnd);
            Text conceptText = prepareTextForConcept(concept, term);
            texts.add(conceptText);
            if (termEnd <= sourceText.length()) {
                start = termEnd;
            }
        }

        if (start < sourceText.length()) {
            Text remainder = new Text(sourceText.substring(start));
            texts.add(remainder);
        }

        return FXCollections.observableList(texts);
    }

    private Text prepareTextForConcept(String conceptSubstring, ObservableReviewedPhenotypicFeature term) {
        // Just one type of text for now
        return new MinedText(conceptSubstring, term);
    }

}
