package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.util.StringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

public class GenotypeStringConverter extends StringConverter<Genotype> {

    private static final GenotypeStringConverter INSTANCE = new GenotypeStringConverter();

    public static GenotypeStringConverter getInstance() {
        return INSTANCE;
    }

    private GenotypeStringConverter() {
    }

    @Override
    public String toString(Genotype object) {
        return object.getCode();
    }

    @Override
    public Genotype fromString(String string) {
        return Genotype.parseCode(string);
    }
}
