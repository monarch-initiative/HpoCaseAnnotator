package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * The controller manages genomic assembly - each variant type has one (and only one).
 */
public abstract class BaseVariantController implements ComponentController<CuratedVariant> {

    protected static final Strand VCF_STRAND = Strand.POSITIVE;
    protected static final CoordinateSystem VCF_COORDINATE_SYSTEM = CoordinateSystem.oneBased();
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseVariantController.class);

    protected final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");
    private final GenomicAssemblyRegistry genomicAssemblyRegistry;
    private final ToggleGroup genomeAssemblyToggleGroup = new ToggleGroup();

    @FXML
    private RadioButton hg18RadioButton;
    @FXML
    private RadioButton hg19RadioButton;
    @FXML
    private RadioButton hg38RadioButton;

    public BaseVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        this.genomicAssemblyRegistry = Objects.requireNonNull(genomicAssemblyRegistry, "Genomic assembly registry must not be null");
    }

    @FXML
    protected void initialize() {
        initializeGenomicAssemblyToggleGroup();
    }

    private void initializeGenomicAssemblyToggleGroup() {
        hg18RadioButton.setToggleGroup(genomeAssemblyToggleGroup);
        hg19RadioButton.setToggleGroup(genomeAssemblyToggleGroup);
        hg38RadioButton.setToggleGroup(genomeAssemblyToggleGroup);

        hg18RadioButton.disableProperty().bind(genomicAssemblyRegistry.hg18ServiceProperty().isNull());
        hg19RadioButton.disableProperty().bind(genomicAssemblyRegistry.hg19ServiceProperty().isNull());
        hg38RadioButton.disableProperty().bind(genomicAssemblyRegistry.hg38ServiceProperty().isNull());

        genomeAssemblyToggleGroup.selectedToggleProperty().addListener(createAssemblyServiceListener());
    }

    private ChangeListener<Toggle> createAssemblyServiceListener() {
        return (obs, old, novel) -> assemblyService.set(genomicAssemblyServiceForRadioButton((RadioButton) novel)
                .orElse(null));
    }

    private Optional<GenomicAssemblyService> genomicAssemblyServiceForRadioButton(RadioButton selectedRadioButton) {
        Optional<GenomicAssemblyService> serviceOptional;
        if (selectedRadioButton.equals(hg18RadioButton)) {
            serviceOptional = Optional.ofNullable(genomicAssemblyRegistry.getHg18Service());
        } else if (selectedRadioButton.equals(hg19RadioButton)) {
            serviceOptional = Optional.ofNullable(genomicAssemblyRegistry.getHg19Service());
        } else if (selectedRadioButton.equals(hg38RadioButton)) {
            serviceOptional = Optional.ofNullable(genomicAssemblyRegistry.getHg38Service());
        } else {
            LOGGER.warn("Unknown radio button `{}`, {}", selectedRadioButton.getId(), selectedRadioButton);
            serviceOptional = Optional.empty();
        }
        return serviceOptional;
    }

    private Optional<RadioButton> buttonForAssembly(String genomicAssemblyName) {
        if (genomicAssemblyName.matches("NCBI36.*")) {
            return Optional.ofNullable(hg18RadioButton);
        } else if (genomicAssemblyName.matches("GRCh37.*")) {
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

    protected String getGenomicAssembly() throws InvalidComponentDataException {
        GenomicAssemblyService genomicAssemblyService = assemblyService.get();
        if (genomicAssemblyService == null)
            throw new InvalidComponentDataException("Genomic assembly is not selected");

        return genomicAssemblyService.genomicAssembly().name();
    }

    protected void setGenomicAssembly(String genomicAssembly) {
        buttonForAssembly(genomicAssembly)
//                .filter(b -> !b.isDisabled())
                .ifPresent(genomeAssemblyToggleGroup::selectToggle);
    }
}
