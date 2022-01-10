package org.monarchinitiative.hpo_case_annotator.forms.format;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.FormatStringConverter;

import java.text.NumberFormat;

public class Formats {

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    private static final FormatStringConverter<Number> NUMBER_FORMAT_STRING_CONVERTER = new FormatStringConverter<>(NUMBER_FORMAT);

    private Formats(){}

    public static TextFormatter<Number> numberFormatter() {
        return new TextFormatter<>(NUMBER_FORMAT_STRING_CONVERTER);
    }


}
