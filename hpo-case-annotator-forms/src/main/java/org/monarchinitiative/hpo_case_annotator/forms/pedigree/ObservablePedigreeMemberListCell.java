package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.property.ListProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

class ObservablePedigreeMemberListCell extends ListCell<ObservablePedigreeMember> {

    private final Region pedigreeMemberGraphic;

    ObservablePedigreeMemberListCell(HCAControllerFactory controllerFactory, ListProperty<CuratedVariant> variants) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FXMLLoader loader = new FXMLLoader(PedigreeMember.class.getResource("PedigreeMember.fxml"));
        loader.setControllerFactory(controllerFactory);
        PedigreeMember pedigreeMember;
        try {
            pedigreeMemberGraphic = loader.load();
            pedigreeMember = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pedigreeMemberGraphic.prefWidthProperty().bind(maxWidthProperty());
        pedigreeMember.itemProperty().bindBidirectional(itemProperty());
        pedigreeMember.variantsProperty().bind(variants);
    }

    @Override
    protected void updateItem(ObservablePedigreeMember item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(pedigreeMemberGraphic);
        }
    }
}
