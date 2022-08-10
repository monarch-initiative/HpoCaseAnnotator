package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public class MinedText extends Text {

    private final ObservableMinedTerm minedTerm;

    private final Tooltip tooltip = new Tooltip();


    public MinedText(ObservableMinedTerm minedTerm, String text) {
        this.minedTerm = minedTerm;
        this.setText(text);
        this.setFill(Color.RED);
        this.setOnMouseClicked(e -> showHpoInfo());
        this.minedTerm.reviewStatusProperty().addListener((obs, old, novel) -> {
            switch (old) {
                // Clean up
                case APPROVED -> getStyleClass().remove("accepted");
                case REJECTED -> getStyleClass().remove("rejected");
                case UNREVIEWED -> {} // no-op
            }
            switch (novel) {
                case APPROVED -> getStyleClass().add("accepted");
                case REJECTED -> getStyleClass().add("rejected");
                case UNREVIEWED -> {} // should not happen
            }
        });
    }

    private void showHpoInfo() {
        MiningResultsTooltipInfo infoBox = new MiningResultsTooltipInfo(minedTerm);
        infoBox.setSpacing(10);

        tooltip.setGraphic(infoBox);
        tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        tooltip.getStyleClass().add("tooltip");
        infoBox.getStyleClass().add("tooltip-box");
        infoBox.getIdLabel().getStyleClass().add("id-label");
        infoBox.getNameLabel().getStyleClass().add("name-label");
        infoBox.getButtonBox().getStyleClass().add("tooltip-box");
        Tooltip.install(this, tooltip);
        tooltip.show(getScene().getWindow());

        minedTerm.reviewStatusProperty().addListener(obs -> tooltip.hide());
    }

    public ObservableMinedTerm getMinedTerm() {
        return minedTerm;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

}
