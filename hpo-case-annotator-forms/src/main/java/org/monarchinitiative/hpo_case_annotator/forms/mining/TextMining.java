package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.core.mining.TextMiningResults;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.util.List;

public class TextMining extends VBox {

    private final ObjectProperty<ObservableTimeElement> encounterTime = new SimpleObjectProperty<>();

    private final ObjectProperty<NamedEntityFinder> namedEntityFinder = new SimpleObjectProperty<>();

    @FXML
    private TabPane contentTabPane;
    @FXML
    private Tab submitTab;
    @FXML
    private Tab reviewTab;
    @FXML
    private TextField payload;
    @FXML
    private Button submitButton;
    @FXML
    private MiningResultsVetting vetting;

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
        submitButton.disableProperty().bind(namedEntityFinder.isNull());
        payload.disableProperty().bind(namedEntityFinder.isNull());
    }

    @FXML
    private void submitInputText(ActionEvent e) {
        NamedEntityFinder finder = namedEntityFinder.get();
        String sourceText = payload.getText();
        List<MinedTerm> terms = finder.process(sourceText);
        TextMiningResults results = TextMiningResults.of(sourceText, terms);

        vetting.setResults(results);
        contentTabPane.getSelectionModel().select(reviewTab);


        // TODO - implement the rest of the functionality - adding the reviewed terms into the phenopacket.
        e.consume();
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

    public NamedEntityFinder getNamedEntityFinder() {
        return namedEntityFinder.get();
    }

    public ObjectProperty<NamedEntityFinder> namedEntityFinderProperty() {
        return namedEntityFinder;
    }

    public void setNamedEntityFinder(NamedEntityFinder namedEntityFinder) {
        this.namedEntityFinder.set(namedEntityFinder);
    }
}
