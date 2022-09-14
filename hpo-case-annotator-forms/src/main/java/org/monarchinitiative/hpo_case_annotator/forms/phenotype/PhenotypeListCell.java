package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

public class PhenotypeListCell extends ListCell<ObservablePhenotypicFeature> {

    private final PhenotypeListCellGraphics graphics = new PhenotypeListCellGraphics();

    public PhenotypeListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        graphics.dataProperty().bind(itemProperty());
    }

    @Override
    protected void updateItem(ObservablePhenotypicFeature item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null)
            setGraphic(null);
        else
            setGraphic(graphics);
    }
}
