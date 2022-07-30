package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeController extends BaseBindingObservableDataController<ObservablePedigree> {

    @FXML
    private Label summary;
    @FXML
    private ListView<ObservablePedigreeMember> members;

    @Override
    protected void initialize() {
        super.initialize();
        members.setCellFactory(ObservablePedigreeMemberListCell::new);
    }

    @Override
    protected void bind(ObservablePedigree data) {
        members.itemsProperty().bindBidirectional(data.membersProperty());
        StringBinding summaryBinding = Bindings.createStringBinding(() -> pedigreeSummary(data), data.membersProperty());
        summary.textProperty().bind(summaryBinding);
    }

    @Override
    protected void unbind(ObservablePedigree data) {
        members.itemsProperty().unbindBidirectional(data.membersProperty());
        summary.textProperty().unbind();
    }

    private static String pedigreeSummary(ObservablePedigree pedigree) {
        return switch (pedigree.membersProperty().size()) {
            case 0 -> "No individuals in the pedigree.";
            case 1 -> "1 individual in the pedigree.";
            default -> "%d individuals in the pedigree".formatted(pedigree.membersProperty().size());
        };
    }
}
