package org.monarchinitiative.hpo_case_annotator.forms.v2.pedigree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.forms.custom.NamedLabel;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PedigreeMemberController {

    private static final Map<ProbandSex, Image> INDIVIDUAL_ICONS = loadIndividualIcons();

    @FXML
    private ImageView individualIcon;
    @FXML
    private Label id;
    @FXML
    private NamedLabel paternalId;
    @FXML
    private NamedLabel maternalId;
    @FXML
    private NamedLabel sex;
    @FXML
    private NamedLabel proband;

    @FXML
    private TableView<ObservablePhenotypicFeature> phenotypes;
    @FXML
    private TableColumn<ObservablePhenotypicFeature, String> idColumn;
    private TableColumn<ObservablePhenotypicFeature, String> labelColumn;
    private TableColumn<ObservablePhenotypicFeature, String> statusColumn;

    private ObjectBinding<ProbandSex> currentProbandSex;

    private final ChangeListener<ProbandSex> probandStateListener = (obs, old, novel) -> {
        if (novel == null)
            individualIcon.setImage(null);
        else
            individualIcon.setImage(INDIVIDUAL_ICONS.getOrDefault(novel, null));
    };

    void bind(ObservablePedigreeMember item) {
        id.textProperty().bind(item.idProperty());
        paternalId.textProperty().bind(item.paternalIdProperty());
        maternalId.textProperty().bind(item.maternalIdProperty());
        sex.textProperty().bind(item.sexProperty().asString());
        StringBinding affectedBinding = Bindings.createStringBinding(() -> item.isProband() ? "Yes" : "No", item.probandProperty());
        proband.textProperty().bind(affectedBinding);

        currentProbandSex = Bindings.createObjectBinding(() -> ProbandSex.forSexAndProband(item.getSex(), item.isProband()), item.sexProperty(), item.probandProperty());
        currentProbandSex.addListener(probandStateListener);
        ProbandSex probandSex = ProbandSex.forSexAndProband(item.getSex(), item.isProband());
        individualIcon.setImage(INDIVIDUAL_ICONS.getOrDefault(probandSex, null));

        Bindings.bindContent(item.phenotypicFeatures(), phenotypes.getItems());
    }

    void unbind(ObservablePedigreeMember item) {
        id.textProperty().unbind();
        paternalId.textProperty().unbind();
        maternalId.textProperty().unbind();
        sex.textProperty().unbind();
        proband.textProperty().unbind();

        currentProbandSex = null;

        Bindings.unbindContent(item.phenotypicFeatures(), phenotypes.getItems());
    }

    private enum ProbandSex {
        MALE_PROBAND("male-proband.png"),
        MALE("male.png"),
        FEMALE_PROBAND("female-proband.png"),
        FEMALE("female.png");

        private final String imageUrl;

        ProbandSex(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        static ProbandSex forSexAndProband(Sex sex, boolean isProband) {
            return switch (sex) {
                case MALE -> isProband ? MALE_PROBAND : MALE;
                case FEMALE -> isProband ? FEMALE_PROBAND :FEMALE;
                case UNKNOWN -> null;
            };
        }

    }

    private static Map<ProbandSex, Image> loadIndividualIcons() {
        Map<ProbandSex, Image> images = new HashMap<>();
        for (ProbandSex value : ProbandSex.values()) {
            try (InputStream is = PedigreeMemberController.class.getResourceAsStream(value.imageUrl)) {
                Image img = new Image(Objects.requireNonNull(is), 50., 50., true, true);
                images.put(value, img);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Map.copyOf(images);
    }

}
