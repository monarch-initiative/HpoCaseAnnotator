package org.monarchinitiative.hpo_case_annotator.forms.v2.pedigree;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class ObservablePedigreeMemberListCell extends ListCell<ObservablePedigreeMember> {

    private final Pane pedigreeMember;
    private final PedigreeMemberController controller = new PedigreeMemberController();

    private ObservablePedigreeMember current;

    ObservablePedigreeMemberListCell(ListView<ObservablePedigreeMember> lv) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FXMLLoader loader = new FXMLLoader(PedigreeMemberController.class.getResource("PedigreeMember.fxml"));
        loader.setController(controller);
        try {
            pedigreeMember = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(ObservablePedigreeMember item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            pedigreeMember.prefWidthProperty().unbind();
            if (current != null)
                controller.unbind(current);
        } else {
            setGraphic(pedigreeMember);
            controller.bind(item);
            pedigreeMember.prefWidthProperty().bind(maxWidthProperty());
        }

        current = item;
    }
}
