package org.monarchinitiative.hpo_case_annotator.forms.utils;

import javafx.util.StringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;

public class SexStringConverter extends StringConverter<Sex> {

    private static final SexStringConverter INSTANCE = new SexStringConverter();

    private SexStringConverter() {
    }

    public static SexStringConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString(Sex object) {
        return (object != null)
                ? object.getLabel()
                : "";
    }

    @Override
    public Sex fromString(String string) {
        return switch (string) {
            case "Male" -> Sex.MALE;
            case "Female" -> Sex.FEMALE;
            default -> Sex.UNKNOWN;
        };
    }
}
