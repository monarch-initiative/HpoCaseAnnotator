package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.variants.detail.BreakendVariantDetail;
import org.monarchinitiative.hpo_case_annotator.forms.variants.detail.SequenceVariantDetail;
import org.monarchinitiative.hpo_case_annotator.forms.variants.detail.SymbolicVariantDetail;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation;

class ObservableCuratedVariantListCell extends ListCell<ObservableCuratedVariant> {

    ObservableCuratedVariantListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(ObservableCuratedVariant variant, boolean empty) {
        super.updateItem(variant, empty);

        if (empty || variant == null)
            setGraphic(null);

        else {
            VariantNotation notation = variant.getVariantNotation();
            if (notation == null) {
                setGraphic(new Label("Missing variant notation"));
                return;
            }

            VBoxObservableDataComponent<ObservableCuratedVariant> detail = switch (notation) {
                case SEQUENCE -> new SequenceVariantDetail();
                case SYMBOLIC -> new SymbolicVariantDetail();
                case BREAKEND -> new BreakendVariantDetail();
            };
            detail.setData(variant);

            setGraphic(detail);
        }
    }

}
