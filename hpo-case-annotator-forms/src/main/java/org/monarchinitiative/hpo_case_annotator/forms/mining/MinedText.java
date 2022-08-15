package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.text.Text;


class MinedText extends Text {

    MinedText(String text, ObservableMinedTerm minedTerm) {
        getStyleClass().addAll("unreviewed", "hpo-term");
        this.setText(text);
        this.setOnMouseClicked(e -> showHpoInfo(minedTerm));
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

    private void showHpoInfo(ObservableMinedTerm minedTerm) {
        Tooltip t = new Tooltip();
        MiningResultsTooltipInfo infoBox = new MiningResultsTooltipInfo(minedTerm);

        t.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        t.setGraphic(infoBox);

        // The tooltip is hidden if the tooltip loses focus or if the user clicks on Approve/Reject button.
        minedTerm.reviewStatusProperty().addListener(obs -> t.hide());
        t.setAutoHide(true);

        // Show the tooltip below the text (+20)
        Point2D pointOnScreen = localToScreen(0, 0);
        t.show(getScene().getWindow(), pointOnScreen.getX(), pointOnScreen.getY() + 20);
    }

}
