package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class MinedText extends Text {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinedText.class);

    MinedText(String text, ObservableReviewedPhenotypicFeature minedTerm) {
        getStyleClass().addAll("unreviewed", "hpo-term");
        this.setText(text);
        this.setOnMouseClicked(e -> showHpoInfo(e, minedTerm));
        minedTerm.reviewStatusProperty().addListener((obs, old, novel) -> {
            switch (old) {
                // Clean up
                case APPROVED -> getStyleClass().remove("approved");
                case REJECTED -> getStyleClass().remove("rejected");
                case UNREVIEWED -> getStyleClass().remove("unreviewed");
            }
            switch (novel) {
                case APPROVED -> getStyleClass().add("approved");
                case REJECTED -> getStyleClass().add("rejected");
                case UNREVIEWED -> {} // should not happen
            }
        });
    }

    private void showHpoInfo(MouseEvent e, ObservableReviewedPhenotypicFeature feature) {
        switch (e.getButton()) {
            case PRIMARY -> {
                // left/primary button was clicked - this means approval.
                LOGGER.debug("Approving feature [{},{}]: {}:{}", feature.start(), feature.end(), feature.id().getValue(), feature.getLabel());
                feature.setReviewStatus(ReviewStatus.APPROVED);
            }
            case SECONDARY -> {
                // right/secondary button was clicked - this means we go through the entire vetting procedure.
                LOGGER.debug("Vetting feature [{},{}]: {}:{}", feature.start(), feature.end(), feature.id().getValue(), feature.getLabel());
                Tooltip t = new Tooltip();
                MiningResultsTooltipInfo infoBox = new MiningResultsTooltipInfo(feature);

                t.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                t.setGraphic(infoBox);

                // The tooltip is hidden if the tooltip loses focus or if the user clicks on Approve/Reject button.
                feature.reviewStatusProperty().addListener(obs -> t.hide());
                t.setAutoHide(true);

                // Show the tooltip below the text (+20)
                Point2D pointOnScreen = localToScreen(0, 0);
                t.show(getScene().getWindow(), pointOnScreen.getX(), pointOnScreen.getY() + 20);
            }
            default -> LOGGER.warn("Unknown mouse event happened: {}", e);
        }

        e.consume();
    }

}
