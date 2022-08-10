package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MiningResultsTooltipInfo extends VBox {

    private final ObservableMinedTerm minedTerm;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private CheckBox excludedCheckBox;

    @FXML
    private Button acceptTermButton;

    @FXML
    private Button rejectTermButton;

    @FXML
    private HBox buttonBox;

    public MiningResultsTooltipInfo(ObservableMinedTerm minedTerm) {
        this.minedTerm = Objects.requireNonNull(minedTerm);
        FXMLLoader loader = new FXMLLoader(MiningResultsVettingBox.class.getResource("MiningResultsTooltipInfo.fxml"));
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
