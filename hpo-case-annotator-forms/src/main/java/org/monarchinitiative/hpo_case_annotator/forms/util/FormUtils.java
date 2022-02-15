package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.svart.Contig;

import java.util.List;
import java.util.stream.Stream;

public class FormUtils {

    private FormUtils(){}

    public static List<Integer> getIntegers(int endInclusive) {
        return Stream.iterate(0, i -> i <= endInclusive, i -> i + 1).toList();
    }

    public static String checkMark() {
        return "\u2713";
    }

    public static String crossMark() {
        return "\u274C";
    }

    public static void initializeContigComboBox(ComboBox<Contig> contigComboBox, ObjectProperty<GenomicAssemblyService> assemblyService) {
        contigComboBox.setConverter(ContigNameStringConverter.getInstance());
        contigComboBox.disableProperty().bind(assemblyService.isNull());
        assemblyService.addListener((obs, old, novel) -> {
            contigComboBox.setValue(null);
            if (novel == null) {
                contigComboBox.getItems().clear();
            } else {
                contigComboBox.getItems().setAll(novel.genomicAssembly().contigs());
            }
        });
    }

    public static int processFormattedInteger(String integerText) throws InvalidComponentDataException {
        try {
            return Integer.parseInt(integerText.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            throw new InvalidComponentDataException(e);
        }
    }
}
