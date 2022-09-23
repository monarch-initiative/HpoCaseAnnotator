package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotation;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotationService;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.liftover.Liftover;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.svart.GenomicVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static javafx.beans.binding.Bindings.*;

/**
 * The controller manages genomic assembly - each variant type has one (and only one).
 *
 * <h2>Properties</h2>
 * {@link BaseVariantDataEdit} needs the following properties to be set in order to work.
 * <ul>
 *     <li>{@link #genomicAssemblyRegistryProperty()}</li>
 *     <li>{@link #functionalAnnotationRegistryProperty()}</li>
 * </ul>
 */
public abstract class BaseVariantDataEdit extends VBoxDataEdit<ObservableCuratedVariant> implements Observable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseVariantDataEdit.class);

    // Names corresponding to Svart's assembly names.
    private static final String HG19_NAME = "GRCh37.p13";
    private static final String HG38_NAME = "GRCh38.p13";

    private static final Callback<BaseVariantDataEdit, Stream<Observable>> EXTRACTOR = vc -> {
        Stream<Observable> dependencies = Stream.of(vc.genomeAssemblyToggleGroup.selectedToggleProperty());
        return Stream.concat(dependencies, vc.dependencies());
    };

    protected ObservableCuratedVariant item;

    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>();
    protected final ObjectProperty<GenomicAssemblyService> genomicAssemblyService = new SimpleObjectProperty<>();

    private final ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistry = new SimpleObjectProperty<>();
    private final ObjectProperty<FunctionalAnnotationService> functionalAnnotationService = new SimpleObjectProperty<>();

    private final ListProperty<FunctionalAnnotation> functionalAnnotations = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ToggleGroup genomeAssemblyToggleGroup = new ToggleGroup();
    @FXML
    private RadioButton hg19RadioButton;
    @FXML
    private RadioButton hg38RadioButton;
    @FXML
    private FunctionalAnnotationTable functionalAnnotationTable;
    @FXML
    private Liftover liftover;

    protected BaseVariantDataEdit(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }
    public ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistryProperty() {
        return functionalAnnotationRegistry;
    }
    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftover.liftoverServiceProperty();
    }

    @FXML
    protected void initialize() {
        initializeGenomicAssemblyToggleGroup();

        functionalAnnotationTable.disableProperty().bind(functionalAnnotationService.isNull());
        functionalAnnotationTable.functionalAnnotations().bind(functionalAnnotations);
        addListener(recalculateFunctionalAnnotationsUponVariantChange());
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        item = Objects.requireNonNull(data);

        buttonForAssembly(data.getGenomicAssembly())
                .ifPresent(genomeAssemblyToggleGroup::selectToggle);
    }

    @Override
    public void commit() {
        Toggle toggle = genomeAssemblyToggleGroup.getSelectedToggle();
        if (toggle.equals(hg19RadioButton)) {
            item.setGenomicAssembly(HG19_NAME);
        } else if (toggle.equals(hg38RadioButton)) {
            item.setGenomicAssembly(HG38_NAME);
        }
    }

    /**
     * @return a stream of {@link Observable} dependencies
     */
    protected abstract Stream<Observable> dependencies();

    protected abstract Optional<GenomicVariant> getVariant();

    private InvalidationListener recalculateFunctionalAnnotationsUponVariantChange() {
        return obs -> {
            FunctionalAnnotationService annotationService = functionalAnnotationService.get();
            if (annotationService != null) {
                try {
                    Optional<GenomicVariant> current = getVariant();
                    if (current.isPresent()) {
                        functionalAnnotations.setAll(annotationService.annotate(current.get()));
                        return;
                    }
                } catch (Exception e) {
                    LOGGER.debug("Incomplete component: {}", e.getMessage());
                }
            }
            // Something must have gone wrong upstream, variant is missing, or let's clean!
            functionalAnnotations.clear();
        };
    }

    private void initializeGenomicAssemblyToggleGroup() {
        hg19RadioButton.setToggleGroup(genomeAssemblyToggleGroup);
        hg38RadioButton.setToggleGroup(genomeAssemblyToggleGroup);

        hg19RadioButton.disableProperty().bind(genomicAssemblyRegistry.isNull().or(select(genomicAssemblyRegistry, "hg19Service").isNull()));
        hg38RadioButton.disableProperty().bind(genomicAssemblyRegistry.isNull().or(select(genomicAssemblyRegistry, "hg38Service").isNull()));

        genomeAssemblyToggleGroup.selectedToggleProperty().addListener(updateServicesOnAssemblyChange());
    }

    private ChangeListener<Toggle> updateServicesOnAssemblyChange() {
        return (obs, old, novel) -> {
            RadioButton assembly = (RadioButton) novel;
            genomicAssemblyService.set(genomicAssemblyServiceForRadioButton(assembly).orElse(null));
            functionalAnnotationService.set(functionalAnnotationServiceForRadioButton(assembly).orElse(null));
        };
    }

    private Optional<GenomicAssemblyService> genomicAssemblyServiceForRadioButton(RadioButton assembly) {
        Optional<GenomicAssemblyService> serviceOptional;
        if (assembly.equals(hg19RadioButton)) {
            serviceOptional = Optional.ofNullable(genomicAssemblyRegistry.get())
                    .map(GenomicAssemblyRegistry::getHg19Service);
        } else if (assembly.equals(hg38RadioButton)) {
            serviceOptional = Optional.ofNullable(genomicAssemblyRegistry.get())
                    .map(GenomicAssemblyRegistry::getHg38Service);
        } else {
            LOGGER.warn("Unknown radio button `{}`, {}", assembly.getId(), assembly);
            serviceOptional = Optional.empty();
        }
        return serviceOptional;
    }

    private Optional<FunctionalAnnotationService> functionalAnnotationServiceForRadioButton(RadioButton assembly) {
        if (assembly.equals(hg19RadioButton)) {
            return Optional.ofNullable(functionalAnnotationRegistry.get())
                    .map(FunctionalAnnotationRegistry::getHg19Service);
        } else if (assembly.equals(hg38RadioButton)) {
            return Optional.ofNullable(functionalAnnotationRegistry.get())
                    .map(FunctionalAnnotationRegistry::getHg38Service);
        } else {
            LOGGER.warn("Unknown radio button `{}`, {}", assembly.getId(), assembly);
            return Optional.empty();
        }
    }

    private Optional<RadioButton> buttonForAssembly(String genomicAssemblyName) {
        if (genomicAssemblyName == null)
            return Optional.empty();

        if (genomicAssemblyName.matches("GRCh37.*")) {
            return Optional.ofNullable(hg19RadioButton);
        } else if (genomicAssemblyName.matches("GRCh38.*")) {
            return Optional.ofNullable(hg38RadioButton);
        } else {
            return Optional.empty();
        }
    }

    protected void presentVariantMetadata(VariantMetadata metadata) {
        // TODO - implement
    }

    protected VariantMetadata getVariantMetadata() throws InvalidComponentDataException {
        // TODO - add real metadata
        return StructuralVariantMetadata.of("", "", "", false, false);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(observable -> observable.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(observable -> observable.removeListener(listener));
    }

}
