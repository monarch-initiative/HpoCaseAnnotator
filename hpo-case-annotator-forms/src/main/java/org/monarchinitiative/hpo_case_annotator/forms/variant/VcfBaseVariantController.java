package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.format.ContigNameStringConverter;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.GenomicAssembly;

import java.util.Objects;
import java.util.Set;

public abstract class VcfBaseVariantController {

    protected final ToggleGroup genomeAssemblyToggleGroup = new ToggleGroup();

    protected final GenomicAssemblyRegistry genomicAssemblyRegistry;
    protected final ObjectProperty<GenomicAssemblyService> assemblyService = new SimpleObjectProperty<>(null, "assemblyService");

    @FXML
    private  RadioButton hg18RadioButton;
    @FXML
    private  RadioButton hg19RadioButton;
    @FXML
    private  RadioButton hg38RadioButton;
    @FXML
    private  ComboBox<Contig> contigComboBox;

    protected void initialize() {
        initializeGenomicAssemblyToggleGroup();
        initializeContigComboBox();
    }

    protected VcfBaseVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        this.genomicAssemblyRegistry = Objects.requireNonNull(genomicAssemblyRegistry, "Genomic assembly registry must not be null");
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

    protected void initializeContigComboBox() {
        contigComboBox.setConverter(ContigNameStringConverter.getInstance());
        assemblyService.addListener((obs, old, novel) -> {
            if (novel == null) {
                contigComboBox.getItems().clear();
            } else {
                GenomicAssembly genomicAssembly = assemblyService.get().genomicAssembly();
                contigComboBox.getItems().addAll(genomicAssembly.contigs());
            }
        });
    }

}
