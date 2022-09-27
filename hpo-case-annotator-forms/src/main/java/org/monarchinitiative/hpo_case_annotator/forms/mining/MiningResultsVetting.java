package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.core.mining.TextMiningResults;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MiningResultsVetting extends VBox {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<TextMiningResults> results = new SimpleObjectProperty<>();

    @FXML
    private TextFlow textFlow;

    @FXML
    private TableView<ObservableMinedTerm> vettedPhenotypicFeatures;
    @FXML
    private TableColumn<ObservableMinedTerm, String> idColumn;
    @FXML
    private TableColumn<ObservableMinedTerm, String> nameColumn;
    @FXML
    private TableColumn<ObservableMinedTerm, Boolean> excludedColumn;
    @FXML
    private TableColumn<ObservableMinedTerm, ReviewStatus> statusColumn;
    @FXML
    private Label statusLabel;

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

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTermId().toString()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLabel()));
        excludedColumn.setCellValueFactory(cellData -> cellData.getValue().isExcludedProperty());
        statusColumn.setCellFactory(c -> new ReviewStatusTableCell());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().reviewStatusProperty());
        results.addListener((obs, old, novel) -> {
            unbind(old);
            bind(novel);
        });
    }

    private void bind(TextMiningResults results) {
        if (results == null) {
            textFlow.getChildren().clear();
            vettedPhenotypicFeatures.getItems().clear();
        } else {
            String sourceText = results.sourceText();

            ObservableList<ObservableMinedTerm> sortedObservableTerms = results.minedTerms().stream()
                    .sorted(Comparator.comparingInt(MinedTerm::start).thenComparingInt(MinedTerm::end))
                    .map(toObservableMinedTerm())
                    .flatMap(Optional::stream)
                    .collect(Collectors.toCollection(() -> FXCollections.observableArrayList(ObservableMinedTerm.EXTRACTOR)));

            ObservableList<Text> texts = prepareTexts(sourceText, sortedObservableTerms);

            textFlow.getChildren().addAll(texts);
            vettedPhenotypicFeatures.setItems(sortedObservableTerms);
            statusLabel.textProperty().bind(makeSummaryBinding(sortedObservableTerms));
        }
    }

    private void unbind(TextMiningResults results) {
        if (results != null) {
            statusLabel.textProperty().unbind();
        }
    }

    private Function<MinedTerm, Optional<ObservableMinedTerm>> toObservableMinedTerm() {
        return t -> {
            Ontology h = hpo.get();
            if (h == null)
                return Optional.empty();

            Term term = h.getTermMap().get(t.id());
            return Optional.of(new ObservableMinedTerm(t.id(), term == null ? "N/A" : term.getName(), t.start(), t.end(), t.isNegated()));
        };
    }

    private ObservableList<Text> prepareTexts(String sourceText, List<ObservableMinedTerm> sortedTerms) {
        int start = 0;

        List<Text> texts = new ArrayList<>();
        for (ObservableMinedTerm term : sortedTerms) {
            int termStart = term.getStart();
            int termEnd = term.getEnd();
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

    private Text prepareTextForConcept(String conceptSubstring, ObservableMinedTerm term) {
        // Just one type of text for now
        return new MinedText(conceptSubstring, term);
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
    public List<ObservableMinedTerm> getVettedPhenotypicFeatures() {
        return vettedPhenotypicFeatures.getItems();
    }

    public static StringBinding makeSummaryBinding(ObservableList<ObservableMinedTerm> minedTerms) {
        return new StringBinding() {
            {
                bind(minedTerms);
            }

            @Override
            public String computeValue() {
                int approved = 0;
                int total = 0;
                int present = 0;
                int excluded = 0;
                for (ObservableMinedTerm term : minedTerms) {
                    if (term.isExcludedProperty().get()) {
                        excluded++;
                    } else if (!term.isExcludedProperty().get()) {
                        present++;
                    }
                    if (term.getReviewStatus().equals(ReviewStatus.APPROVED)) {
                        approved++;
                    }
                    total++;
                }
                return "%s total: %s approved, %s present, %s excluded".formatted(total, approved, present, excluded);
            }

        };
    }

}
