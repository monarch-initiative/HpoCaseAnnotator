package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MiningResultsTooltipInfo extends VBox {

    private final MinedText minedText;

    @FXML
    private Label idLabel = new Label();

    @FXML
    private Label nameLabel = new Label();

    @FXML
    private final CheckBox excludedCheckBox = new CheckBox();

    @FXML
    private Button acceptTermButton;

    @FXML
    private Button rejectTermButton;

    public MiningResultsTooltipInfo(MinedText minedText) {
        this.minedText = minedText;
        FXMLLoader loader = new FXMLLoader(MiningResultsVettingBox.class.getResource("MiningResultsTooltipInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            initialize();
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        ObservableMinedTerm minedTerm = minedText.getMinedTerm();
        String id = minedTerm.getTermId().toString();
        String name = minedTerm.getLabel();
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public CheckBox getExcludedCheckBox() {
        return excludedCheckBox;
    }

    @FXML
    void acceptTermButtonAction() {
        acceptTerm(true);
    }

    @FXML
    void rejectTermButtonAction() {
        acceptTerm(false);
    }

    void acceptTerm(boolean accept) {
        if (accept) {
            minedText.getStyleClass().add("accepted");
        } else {
            minedText.getStyleClass().remove("accepted");
        }
        minedText.getMinedTerm().isApprovedProperty().setValue(accept);
        minedText.getTooltip().hide();
    }
}
