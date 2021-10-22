package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.svart.GenomicAssembly;

import java.util.Objects;
import java.util.Set;

public class VcfBreakendVariantController {

    protected final ToggleGroup genomeAssemblyToggleGroup = new ToggleGroup();

    protected final GenomicAssemblyRegistry genomicAssemblyRegistry;
    protected final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");

    public RadioButton hg18RadioButton;
    public RadioButton hg19RadioButton;
    public RadioButton hg38RadioButton;

    public VBox leftBreakend;
    public BreakendController leftBreakendController;
    public VBox rightBreakend;
    public BreakendController rightBreakendController;

    public TextField insertedSequenceTextField;

    public VcfBreakendVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        this.genomicAssemblyRegistry = Objects.requireNonNull(genomicAssemblyRegistry, "Genomic assembly registry must not be null");
    }


    public void initialize() {
        initializeGenomicAssemblyToggleGroup();
        initializeContigComboBoxes();
    }

    private void initializeGenomicAssemblyToggleGroup() {
        hg18RadioButton.setToggleGroup(genomeAssemblyToggleGroup);
        hg19RadioButton.setToggleGroup(genomeAssemblyToggleGroup);
        hg38RadioButton.setToggleGroup(genomeAssemblyToggleGroup);


        Set<String> assemblyNames = genomicAssemblyRegistry.assemblyNames();
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

    private void initializeContigComboBoxes() {
        assemblyService.addListener((obs, old, novel) -> {
            if (novel == null) {
                leftBreakendController.contigComboBox.getItems().clear();
                rightBreakendController.contigComboBox.getItems().clear();
            } else {
                GenomicAssembly genomicAssembly = assemblyService.get().genomicAssembly();
                leftBreakendController.contigComboBox.getItems().addAll(genomicAssembly.contigs());
                rightBreakendController.contigComboBox.getItems().addAll(genomicAssembly.contigs());
            }
        });
    }
}
