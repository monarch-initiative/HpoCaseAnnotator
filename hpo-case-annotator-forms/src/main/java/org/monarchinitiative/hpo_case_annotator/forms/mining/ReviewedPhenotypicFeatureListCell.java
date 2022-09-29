package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

class ReviewedPhenotypicFeatureListCell extends ListCell<ObservableReviewedPhenotypicFeature> {

    private final ReviewedPhenotypeListCellGraphics graphics = new ReviewedPhenotypeListCellGraphics();

    ReviewedPhenotypicFeatureListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        graphics.dataProperty().bind(itemProperty());
    }

    @Override
    protected void updateItem(ObservableReviewedPhenotypicFeature item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null)
            setGraphic(null);
        else
            setGraphic(graphics);
    }
}
