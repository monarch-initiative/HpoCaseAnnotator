package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MiningResultsTooltipInfo extends VBox {

    private final MinedText minedText;

    @FXML
    private Label idLabel = new Label();

    @FXML
    private Label nameLabel = new Label();

    @FXML
    private CheckBox excludedCheckBox = new CheckBox();

    @FXML
    private Button acceptTermButton;

    @FXML
    private Button rejectTermButton;

    @FXML
    private HBox buttonBox;

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
        excludedCheckBox.selectedProperty().bindBidirectional(minedTerm.isExcludedProperty());
//        acceptTermButton.setTooltip(new Tooltip("Accept Term"));
//        rejectTermButton.setTooltip(new Tooltip("Reject Term"));
    }

    public CheckBox getExcludedCheckBox() {
        return excludedCheckBox;
    }

    public HBox getButtonBox() {
        return buttonBox;
    }

    public Label getIdLabel() {
        return idLabel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    @FXML
    void acceptTermButtonAction() {
        minedTerm.setReviewStatus(ReviewStatus.APPROVED);
    }

    @FXML
    void rejectTermButtonAction() {
        minedTerm.setReviewStatus(ReviewStatus.REJECTED);
    }

}
