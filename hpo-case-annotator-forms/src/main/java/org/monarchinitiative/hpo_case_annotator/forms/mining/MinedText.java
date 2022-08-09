package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Point2D;
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
    }

    private void showHpoInfo() {
        MiningResultsTooltipInfo infoBox = new MiningResultsTooltipInfo(this);
        infoBox.setSpacing(10);
        CheckBox excludedCheckBox = infoBox.getExcludedCheckBox();
        BooleanProperty excludedProperty = minedTerm.isExcludedProperty();
        excludedCheckBox.setSelected(excludedProperty.get());
        excludedProperty.bind(excludedCheckBox.selectedProperty());

        tooltip.setGraphic(infoBox);
        tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        tooltip.getStyleClass().add("tooltip");
        Tooltip.install(this, tooltip);
        tooltip.show(this.getScene().getWindow());
//        Point2D p = this.localToScene(0.0, 0.0);
//        double xCoord = p.getX() + this.getScene().getX() + this.getScene().getWindow().getX();
//        double yCoord = p.getY() + this.getScene().getY() + this.getScene().getWindow().getY() + 15;
//        tooltip.show(this, xCoord, yCoord);
    }

    public ObservableMinedTerm getMinedTerm() {
        return minedTerm;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

}
