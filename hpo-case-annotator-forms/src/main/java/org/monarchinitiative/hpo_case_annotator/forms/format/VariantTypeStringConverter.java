package org.monarchinitiative.hpo_case_annotator.forms.format;

import javafx.util.StringConverter;
import org.monarchinitiative.svart.VariantType;

public class VariantTypeStringConverter extends StringConverter<VariantType> {

    private static final VariantTypeStringConverter INSTANCE = new VariantTypeStringConverter();

    public static VariantTypeStringConverter getInstance() {
        return INSTANCE;
    }

    private VariantTypeStringConverter(){}

    @Override
    public String toString(VariantType object) {
        if (object == null)
            return "";

        return switch (object.baseType()) {
            case SNV -> "Single-nucleotide variant";
            case MNV -> "Multi-nucleotide variant";
            case DEL -> "Deletion";
            case INS -> "Insertion";
            case DUP -> "Duplication";
            case INV -> "Inversion";
            case BND, TRA -> "Translocation";
            case CNV -> "Copy number variation";
            default -> "Unknown";
        };
    }

    @Override
    public VariantType fromString(String string) {
        return VariantType.parseType(string);
    }
}
