package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.DeprecatedGenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The controller manages genomic assembly - each variant type has one (and only one).
 */
public abstract class UnifiedBaseVariantController<T> {

    protected final DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry;
    protected final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");
    protected final ToggleGroup genomeAssemblyToggleGroup = new ToggleGroup();
    protected final ObjectProperty<T> variant = new SimpleObjectProperty<>(this, "variant");
    @FXML
    protected RadioButton hg18RadioButton;
    @FXML
    protected RadioButton hg19RadioButton;
    @FXML
    protected RadioButton hg38RadioButton;

    public UnifiedBaseVariantController(DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry) {
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


        Set<String> assemblyNames = genomicAssemblyRegistry.assemblyNames().collect(Collectors.toUnmodifiableSet());
        if (assemblyNames.stream().anyMatch(name -> name.toUpperCase().matches("NCBI36.*"))) {
            hg18RadioButton.setDisable(false);
        }
        if (assemblyNames.stream().anyMatch(name -> name.toUpperCase().matches("GRCH37.*"))) {
            hg19RadioButton.setDisable(false);
        }
        if (assemblyNames.stream().anyMatch(name -> name.toUpperCase().matches("GRCH38.*"))) {
            hg38RadioButton.setDisable(false);
        }

        genomeAssemblyToggleGroup.selectedToggleProperty().addListener((obs, old, novel) -> {
            RadioButton selected = (RadioButton) obs.getValue();
            switch (selected.getId()) {
                case "hg18RadioButton" -> genomicAssemblyRegistry.assemblyForName("NCBI36").ifPresent(assemblyService::set);
                case "hg19RadioButton" -> genomicAssemblyRegistry.assemblyForName("GRCh37.p13").ifPresent(assemblyService::set);
                case "hg38RadioButton" -> genomicAssemblyRegistry.assemblyForName("GRCh38.p13").ifPresent(assemblyService::set);
                default -> throw new RuntimeException("Unknown radio button ID: " + selected.getId());
            }
        });
    }

    protected abstract ChangeListener<T> variantChangeListener();

    protected void unbind(T variant) {

    }

    protected void bind(T variant) {

    }

    public T getVariant() {
        return variant.get();
    }

    public void setVariant(T variant) {
        this.variant.set(variant);
    }

    public ObjectProperty<T> variantProperty() {
        return variant;
    }

}
