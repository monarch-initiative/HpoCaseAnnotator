package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.monarchinitiative.hpo_case_annotator.forms.BaseBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller("nvoPedigreeController") // TODO - rename
public class PedigreeController extends BaseBindingObservableDataController<ObservablePedigree> {

    private final HCAControllerFactory controllerFactory;
    private final ListProperty<CuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());
    @FXML
    private Label summary;
    @FXML
    private ListView<ObservablePedigreeMember> members;

    public PedigreeController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @Override
    protected void initialize() {
        super.initialize();
        members.setCellFactory(lv -> new ObservablePedigreeMemberListCell(controllerFactory, variants));
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

    public ListProperty<CuratedVariant> variantsProperty() {
        return variants;
    }

    private static String pedigreeSummary(ObservablePedigree pedigree) {
        return switch (pedigree.membersProperty().size()) {
            case 0 -> "No individuals in the pedigree.";
            case 1 -> "1 individual in the pedigree.";
            default -> "%d individuals in the pedigree".formatted(pedigree.membersProperty().size());
        };
    }
}
