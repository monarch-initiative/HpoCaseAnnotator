package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigree;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;

/**
 * {@link Pedigree} manages a list of {@link org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember}s.
 *
 * <h2>Properties</h2>
 * {@link Pedigree} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 *     <li>{@link #hpoProperty()}</li>
 * </ul>
 * <p>
 * Furthermore, the {@link Pedigree} tracks {@link #variantsProperty()} to keep variant genotype tables of the managed
 * {@link org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember}s.
 */
public class Pedigree extends VBoxBindingObservableDataController<ObservablePedigree> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private final ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService = new SimpleObjectProperty<>();
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private Label summary;
    @FXML
    private ListView<ObservablePedigreeMember> members;

    public Pedigree() {
        FXMLLoader loader = new FXMLLoader(Pedigree.class.getResource("Pedigree.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return diseaseIdentifierService;
    }

    @Override
    protected void initialize() {
        super.initialize();
        members.setCellFactory(lv -> new ObservablePedigreeMemberListCell(variants, hpo, diseaseIdentifierService));
    }

    @Override
    protected void bind(ObservablePedigree data) {
        if (data != null) {
            members.itemsProperty().bindBidirectional(data.membersProperty());
            StringBinding summaryBinding = Bindings.createStringBinding(() -> pedigreeSummary(data), data.membersProperty());
            summary.textProperty().bind(summaryBinding);
        }
    }

    @Override
    protected void unbind(ObservablePedigree data) {
        if (data != null) {
            members.itemsProperty().unbindBidirectional(data.membersProperty());
            summary.textProperty().unbind();
        }
    }

    private static String pedigreeSummary(ObservablePedigree pedigree) {
        return switch (pedigree.membersProperty().size()) {
            case 0 -> "No individuals in the pedigree.";
            case 1 -> "1 individual in the pedigree.";
            default -> "%d individuals in the pedigree".formatted(pedigree.membersProperty().size());
        };
    }
}
