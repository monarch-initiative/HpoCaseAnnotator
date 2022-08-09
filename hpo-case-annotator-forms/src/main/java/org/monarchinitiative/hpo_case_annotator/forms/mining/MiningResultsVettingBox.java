package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.binding.ListBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class MiningResultsVettingBox {

    private final Ontology hpo;
    private final ObjectProperty<TextMiningResults> results = new SimpleObjectProperty<>();

    @FXML
    private TextFlow textFlow;
    @FXML
    private TableView<VettedPhenotypicFeature> vettedPhenotypicFeatures;
    @FXML
    private TableColumn<VettedPhenotypicFeature, String> idColumn;
    @FXML
    private TableColumn<VettedPhenotypicFeature, String> nameColumn;
    @FXML
    private TableColumn<VettedPhenotypicFeature, Boolean> excludedColumn;
    @FXML
    private Label statusLabel;

    public MiningResultsVettingBox(Ontology hpo) {
        this.hpo = Objects.requireNonNull(hpo);
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().id().toString()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLabel()));
        excludedColumn.setCellValueFactory(cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isNegated()));
        results.addListener((obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        });
    }

    private void bind(TextMiningResults results) {
        String sourceText = results.sourceText();
        List<MinedTerm> sortedTerms = results.minedTerms().stream().sorted(Comparator.comparingInt(MinedTerm::start)).toList();
        ObservableList<ObservableMinedTerm> minedTerms = FXCollections.observableArrayList(ObservableMinedTerm.EXTRACTOR);
        sortedTerms.forEach(term -> minedTerms.add(new ObservableMinedTerm(term.id(), hpo.getTermMap().get(term.id()).getName(), term.isNegated())));

        List<Text> textList = new ArrayList<>();
        int start = 0;
        for (MinedTerm term : sortedTerms) {
            int termStart = term.start();
            int termEnd = term.end();
            if (start < termStart) {
                Text subText = new Text(sourceText.substring(start, termStart));
                textList.add(subText);
            }
            String minedSubstring = sourceText.substring(termStart, termEnd);
            ObservableMinedTerm observableMinedTerm = minedTerms.get(sortedTerms.indexOf(term));
            MinedText minedText = new MinedText(observableMinedTerm, minedSubstring);
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

        ListBinding<VettedPhenotypicFeature> vettedList = makeListBinding(minedTerms);
        vettedPhenotypicFeatures.itemsProperty().bind(vettedList);

        StringBinding status = makeSummaryBinding(minedTerms);
        statusLabel.textProperty().bind(status);
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

    /**
     * Call the method when done with text mining result vetting to get the list of approved terms.
     *
     * @return the list of vetted phenotypic features.
     */
    public List<VettedPhenotypicFeature> getVettedPhenotypicFeatures() {
        return vettedPhenotypicFeatures.getItems();
    }

    public static StringBinding makeSummaryBinding(ObservableList<ObservableMinedTerm> minedTerms) {
        // TODO - check DoubleBinding for documentation on StringBinding
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
                    if (term.isApprovedProperty().get()) {
                        approved++;
                    }
                    total++;
                }
                return "%s total: %s approved, %s present, %s excluded".formatted(total, approved, present, excluded);
            }

        };
    }

    public static ListBinding<VettedPhenotypicFeature> makeListBinding(ObservableList<ObservableMinedTerm> minedTerms) {
        return new ListBinding<>() {
            {
                bind(minedTerms);
            }

            @Override
            public ObservableList<VettedPhenotypicFeature> computeValue() {
                ObservableList<VettedPhenotypicFeature> vettedFeatures = FXCollections.observableArrayList();
                for (ObservableMinedTerm term : minedTerms) {
                    VettedPhenotypicFeature vettedFeature = new VettedPhenotypicFeature() {
                        @Override
                        public boolean isNegated() {
                            return term.isExcludedProperty().get();
                        }

                        @Override
                        public TermId id() {
                            return term.getTermId();
                        }
                        @Override
                        public String getLabel() {
                            return term.getLabel();
                        }
                    };

                    boolean approved = term.isApprovedProperty().get();
                    boolean inList = vettedFeatures.contains(vettedFeature);
                    if (approved && !inList) {
                        vettedFeatures.add(vettedFeature);
                    } else if (!approved && inList) {
                        vettedFeatures.remove(vettedFeature);
                    }
                }
                return vettedFeatures;
            }

        };
    }

}
