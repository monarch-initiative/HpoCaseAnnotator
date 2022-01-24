package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.text.NumberFormat;

public class Formats {

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    private static final FormatStringConverter<Number> NUMBER_FORMAT_STRING_CONVERTER = new FormatStringConverter<>(NUMBER_FORMAT);
    public static final IntegerStringConverter INTEGER_STRING_CONVERTER = new IntegerStringConverter();

    private Formats(){}

    public static TextFormatter<Number> numberFormatter() {
        return new TextFormatter<>(NUMBER_FORMAT_STRING_CONVERTER);
    }

    public static TextFormatter<Integer> integerFormatter() {
        return new TextFormatter<>(INTEGER_STRING_CONVERTER);
    }

}
