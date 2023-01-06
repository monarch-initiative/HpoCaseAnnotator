package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.text.NumberFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatters {

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    // A positive int OR empty string.
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^([1-9][0-9]*|)$");
    // Zero or positive int.
    private static final Pattern NON_NEGATIVE_INTEGER_PATTERN = Pattern.compile("^(0|([1-9][0-9]*))$");
    private static final Pattern VCF_ALLELE_PATTERN = Pattern.compile("^[ACGTN]*$", Pattern.CASE_INSENSITIVE);
    private static final FormatStringConverter<Number> NUMBER_FORMAT_STRING_CONVERTER = new FormatStringConverter<>(NUMBER_FORMAT);
    private static final IntegerStringConverter INTEGER_STRING_CONVERTER = new IntegerStringConverter();
    private static final UnaryOperator<TextFormatter.Change> POSITIVE_INT_FILTER = change -> {
        Matcher matcher = POSITIVE_INTEGER_PATTERN.matcher(change.getControlNewText());
        if (!matcher.matches()) {
             change.setText("");
             change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };
    private static final UnaryOperator<TextFormatter.Change> NONNEGATIVE_INT_FILTER = change -> {
        Matcher matcher = NON_NEGATIVE_INTEGER_PATTERN.matcher(change.getControlNewText());
        if (!matcher.matches()) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };
    private static final UnaryOperator<TextFormatter.Change> VCF_ALLELE_FILTER = change -> {
        Matcher matcher = VCF_ALLELE_PATTERN.matcher(change.getText());
        if (matcher.matches()) {
            change.setText(change.getText().toUpperCase());
        } else {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    private TextFormatters(){}

    public static TextFormatter<Number> numberFormatter() {
        return new TextFormatter<>(NUMBER_FORMAT_STRING_CONVERTER);
    }

    public static TextFormatter<Integer> integerFormatter() {
        return new TextFormatter<>(INTEGER_STRING_CONVERTER);
    }

    public static TextFormatter<Integer> nonNegativeIntegerFormatter() {
        return new TextFormatter<>(INTEGER_STRING_CONVERTER, null, NONNEGATIVE_INT_FILTER);
    }

    public static TextFormatter<Integer> positiveIntegerFormatter() {
        return new TextFormatter<>(INTEGER_STRING_CONVERTER, null, POSITIVE_INT_FILTER);
    }

    public static TextFormatter<String> vcfAlleleFormatter() {
        return new TextFormatter<>(TextFormatter.IDENTITY_STRING_CONVERTER, null, VCF_ALLELE_FILTER);
    }
}
