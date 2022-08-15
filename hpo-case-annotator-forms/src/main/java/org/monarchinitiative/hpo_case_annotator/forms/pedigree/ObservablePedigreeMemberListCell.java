package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.phenol.ontology.data.Ontology;

class ObservablePedigreeMemberListCell extends ListCell<ObservablePedigreeMember> {

    private final PedigreeMember pedigreeMember;

    ObservablePedigreeMemberListCell(ListProperty<CuratedVariant> variants, ObjectProperty<Ontology> hpo) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        pedigreeMember = new PedigreeMember();
        pedigreeMember.prefWidthProperty().bind(maxWidthProperty());
        pedigreeMember.itemProperty().bind(itemProperty());
        pedigreeMember.variantsProperty().bind(variants);
        pedigreeMember.hpoProperty().bind(hpo);
    }

    @Override
    protected void updateItem(ObservablePedigreeMember item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(pedigreeMember);
        }
    }
}
