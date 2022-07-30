package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;


class ObservablePedigreeMemberListCell extends ListCell<ObservablePedigreeMember> {

    private final PedigreeMember pedigreeMember = new PedigreeMember();

    ObservablePedigreeMemberListCell(ListView<ObservablePedigreeMember> lv) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        pedigreeMember.itemProperty().bindBidirectional(itemProperty());
        pedigreeMember.prefWidthProperty().bind(maxWidthProperty());
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
