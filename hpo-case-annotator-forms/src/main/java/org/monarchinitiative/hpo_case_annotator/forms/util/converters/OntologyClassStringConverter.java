package org.monarchinitiative.hpo_case_annotator.forms.util.converters;

import javafx.util.StringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;

public class OntologyClassStringConverter extends StringConverter<OntologyClass> {

    @Override
    public String toString(OntologyClass object) {
        return object == null
                ? null
                : object.getLabel();
    }

    @Override
    public OntologyClass fromString(String string) {
        throw new RuntimeException("Conversion of string to OntologyClass is not supported");
    }

}
