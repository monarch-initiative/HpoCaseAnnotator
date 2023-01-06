package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.mining.NamedEntityFinder;
import org.monarchinitiative.hpo_case_annotator.forms.individual.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static javafx.beans.binding.Bindings.*;

/**
 * {@link PedigreeMemberPane} presents a single {@link org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember}.
 *
 * <h2>Properties</h2>
 * {@link PedigreeMemberPane} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #hpoProperty()}</li>
 *     <li>{@link #namedEntityFinderProperty()}</li>
 *     <li>{@link #diseaseIdentifierServiceProperty()}</li>
 * </ul>
 * <p>
 * Furthermore, the {@link PedigreeMemberPane} tracks {@link #variantsProperty()} to keep track of genotypes
 * of the managed {@link org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember}.
 */
public class PedigreeMemberPane extends TitledPane {

    private static final Map<Sex, Map<Boolean, Image>> SEX_AFFECTED_ICONS = loadSexAffectedIconsMap();
    private final ObjectProperty<ObservablePedigreeMember> data = new SimpleObjectProperty<>();

    @FXML
    private PedigreeMemberTitle pedigreeMemberTitle;
    @FXML
    private PedigreeMember pedigreeMember;

    public PedigreeMemberPane() {
        FXMLLoader loader = new FXMLLoader(PedigreeMemberPane.class.getResource("PedigreeMemberPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<ObservablePedigreeMember> dataProperty() {
        return data;
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return pedigreeMember.variantsProperty();
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return pedigreeMember.hpoProperty();
    }

    public ObjectProperty<NamedEntityFinder> namedEntityFinderProperty() {
        return pedigreeMember.namedEntityFinderProperty();
    }

    public ObjectProperty<DiseaseIdentifierService> diseaseIdentifierServiceProperty() {
        return pedigreeMember.diseaseIdentifierServiceProperty();
    }

    @FXML
    private void initialize() {
        // Individual credentials
        pedigreeMemberTitle.probandId.textProperty().bind(Utils.nullableStringProperty(data, "id"));
        pedigreeMemberTitle.icon.imageProperty().bind(createIndividualImageBinding());
        pedigreeMemberTitle.summary.textProperty().bind(individualSummary(data));

        pedigreeMember.dataProperty().bind(data);
    }

    private ObservableValue<? extends Image> createIndividualImageBinding() {
        ObjectBinding<Sex> sex = select(data, "sex");
        BooleanBinding isProband = selectBoolean(data, "proband");
        return new ObjectBinding<>() {
            {
                bind(sex, isProband);
            }

            @Override
            protected Image computeValue() {
                boolean proband = isProband.get();
                Sex s = sex.get() == null ? Sex.UNKNOWN : sex.get();

                Map<Boolean, Image> sexMap = SEX_AFFECTED_ICONS.get(s);
                return (sexMap == null)
                        ? null
                        : sexMap.get(proband);
            }
        };
    }

    private static StringBinding individualSummary(ObjectProperty<ObservablePedigreeMember> item) {
        ObjectBinding<ObservablePhenotypicFeature> features = select(item, "phenotypicFeatures");

        return new StringBinding() {
            {
                bind(item, features);
            }

            @Override
            protected String computeValue() {
                ObservablePedigreeMember member = item.get();
                if (member == null)
                    return "";

                return new StringBuilder()
                        .append(member.getPhenotypicFeatures().size()).append(" phenotype terms, ")
                        .append(member.getDiseaseStates().size()).append(" disease states, ")
                        .append(member.getGenotypes().stream().filter(ovg -> ovg.getGenotype().isKnown()).count()).append(" known genotypes")
                        .toString();
            }
        };
    }

    // TODO - move to an Util class with lazy loading.
    private static Map<Sex, Map<Boolean, Image>> loadSexAffectedIconsMap() {
        Map<Boolean, Image> female = new HashMap<>();
        female.put(true, loadImage("female-proband.png"));
        female.put(false, loadImage("female.png"));

        Map<Boolean, Image> male = new HashMap<>();
        male.put(true, loadImage("male-proband.png"));
        male.put(false, loadImage("male.png"));

        return Map.of(Sex.MALE, male, Sex.FEMALE, female);
    }

    private static Image loadImage(String location) {
        try (InputStream is = PedigreeMemberPane.class.getResourceAsStream(location)) {
            return new Image(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
