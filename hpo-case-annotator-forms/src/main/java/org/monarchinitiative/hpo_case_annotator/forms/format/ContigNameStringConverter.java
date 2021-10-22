package org.monarchinitiative.hpo_case_annotator.forms.format;

import javafx.util.StringConverter;
import org.monarchinitiative.svart.Contig;

public class ContigNameStringConverter extends StringConverter<Contig> {

    private static final ContigNameStringConverter INSTANCE = new ContigNameStringConverter();

    private ContigNameStringConverter() {
    }

    public static ContigNameStringConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString(Contig object) {
        return object == null ? null : object.name();
    }

    @Override
    public Contig fromString(String string) {
        throw new UnsupportedOperationException("Converting string to Contig is not supported here!");
    }
}
