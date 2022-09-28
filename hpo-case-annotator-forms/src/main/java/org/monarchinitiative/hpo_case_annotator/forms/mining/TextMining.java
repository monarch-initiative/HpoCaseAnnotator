package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TextMining extends VBox {

    static final String N_A = "N/A";
    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableTimeElement> encounterTime = new SimpleObjectProperty<>();

    private final ObjectProperty<NamedEntityFinder> namedEntityFinder = new SimpleObjectProperty<>();
    // This is the single source of truth for all phenotypic features.
    private final ListProperty<ObservableReviewedPhenotypicFeature> reviewedFeatures = new SimpleListProperty<>(FXCollections.observableArrayList(ObservableReviewedPhenotypicFeature.EXTRACTOR));

    @FXML
    private AnchorPane content;

    /* *********************************** SUBMIT SECTION *********************************** */
    @FXML
    private VBox submit;
    @FXML
    private TextArea payload;
    @FXML
    private Button submitButton;

    /* *********************************** REVIEW SECTION *********************************** */
    @FXML
    private VBox review;
    @FXML
    private MiningResultsVetting vetting;
    @FXML
    private Button startOverButton;

    /* *********************************** REVIEW SECTION *********************************** */
    @FXML
    private ListView<ObservableReviewedPhenotypicFeature> reviewedPhenotypicFeatures;

    public TextMining() {
        FXMLLoader loader = new FXMLLoader(TextMining.class.getResource("TextMining.fxml"));
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
        // We always start with the Submit section.
        setSectionContent(submit);
        submitButton.disableProperty().bind(namedEntityFinder.isNull());
        payload.disableProperty().bind(namedEntityFinder.isNull());

        reviewedPhenotypicFeatures.setCellFactory(lv -> new ReviewedPhenotypicFeatureListCell());
        // The first list will be erased and take on the contents of the second list as part of the binding process.
        // That is what we want!
        Bindings.bindContentBidirectional(reviewedPhenotypicFeatures.getItems(), reviewedFeatures);
        reviewedFeatures.addListener(vetting.updateTexts());
        // We will always have up-to-date text in the vetting section.
        vetting.sourceTextProperty().bind(payload.textProperty());
    }

    @FXML
    private void submitInputText(ActionEvent e) {
        NamedEntityFinder finder = namedEntityFinder.get();

        List<ObservableReviewedPhenotypicFeature> features = finder.process(payload.getText()).stream()
                .sorted(Comparator.comparingInt(MinedTerm::start).thenComparingInt(MinedTerm::end))
                .map(toObservableMinedTerm(hpo.get()))
                .flatMap(Optional::stream)
                .toList();
        reviewedFeatures.addAll(features);

        setSectionContent(review);
        e.consume();
    }

    @FXML
    private void startOverAction(ActionEvent e) {
        reviewedPhenotypicFeatures.getItems().clear();
        payload.clear();
        setSectionContent(submit);
        e.consume();
    }

    private static Function<MinedTerm, Optional<ObservableReviewedPhenotypicFeature>> toObservableMinedTerm(Ontology hpo) {
        return minedTerm -> {
            if (hpo == null)
                // This entire widget does not work without HPO.
                return Optional.empty();

            Term term = hpo.getTermMap().get(minedTerm.id());

            ObservableReviewedPhenotypicFeature pf = new ObservableReviewedPhenotypicFeature(minedTerm.start(), minedTerm.end());
            pf.setTermId(minedTerm.id());
            pf.setLabel(term == null ? N_A : term.getName());
            pf.setExcluded(minedTerm.isNegated());

            return Optional.of(pf);
        };
    }

    /**
     * This method is the only place where we set content of the {@link #content}.
     */
    private void setSectionContent(Node node) {
        content.getChildren().clear();
        content.getChildren().add(node);
        AnchorPane.setTopAnchor(node, 0.);
        AnchorPane.setRightAnchor(node, 0.);
        AnchorPane.setBottomAnchor(node, 0.);
        AnchorPane.setLeftAnchor(node, 0.);
    }

    public ObservableTimeElement getEncounterTime() {
        return encounterTime.get();
    }

    public ObjectProperty<ObservableTimeElement> encounterTimeProperty() {
        return encounterTime;
    }

    public void setEncounterTime(ObservableTimeElement encounterTime) {
        this.encounterTime.set(encounterTime);
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<NamedEntityFinder> namedEntityFinderProperty() {
        return namedEntityFinder;
    }

    public ObservableList<ObservableReviewedPhenotypicFeature> getReviewedPhenotypicFeatures() {
        return reviewedFeatures;
    }

}
