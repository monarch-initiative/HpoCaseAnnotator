package org.monarchinitiative.hpo_case_annotator.forms.v2.pedigree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeController extends BaseBindingDataController<ObservablePedigree> {

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
        Bindings.bindContent(members.getItems(), data.membersList());
        StringBinding summaryBinding = Bindings.createStringBinding(() -> pedigreeSummary(data), data.membersList());
        summary.textProperty().bind(summaryBinding);
    }

    private static String pedigreeSummary(ObservablePedigree pedigree) {
        return switch (pedigree.membersList().size()) {
            case 0 -> "No individuals in the pedigree.";
            case 1 -> "1 individual in the pedigree.";
            default -> "%d individuals in the pedigree".formatted(pedigree.membersList().size());
        };
    }

    @Override
    protected void unbind(ObservablePedigree data) {
        Bindings.unbindContent(members.getItems(), data.membersList());
        summary.textProperty().unbind();
    }

    @Override
    protected ObservablePedigree defaultInstance() {
        return new ObservablePedigree();
    }
}
