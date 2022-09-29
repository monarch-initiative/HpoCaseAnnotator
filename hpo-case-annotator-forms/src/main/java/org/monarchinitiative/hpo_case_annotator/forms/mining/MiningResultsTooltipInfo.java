package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MiningResultsTooltipInfo extends VBox {

    private final ObservableReviewedPhenotypicFeature minedTerm;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private CheckBox excludedCheckBox;

    public MiningResultsTooltipInfo(ObservableReviewedPhenotypicFeature minedTerm) {
        this.minedTerm = Objects.requireNonNull(minedTerm);
        FXMLLoader loader = new FXMLLoader(MiningResultsVetting.class.getResource("MiningResultsTooltipInfo.fxml"));
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
        idLabel.setText(minedTerm.getTermId().toString());
        nameLabel.setText(minedTerm.getLabel());
        excludedCheckBox.selectedProperty().bindBidirectional(minedTerm.excludedProperty());
    }

    @FXML
    private void approveTermButtonAction() {
        minedTerm.setReviewStatus(ReviewStatus.APPROVED);
    }

    @FXML
    private void rejectTermButtonAction() {
        minedTerm.setReviewStatus(ReviewStatus.REJECTED);
    }

}
