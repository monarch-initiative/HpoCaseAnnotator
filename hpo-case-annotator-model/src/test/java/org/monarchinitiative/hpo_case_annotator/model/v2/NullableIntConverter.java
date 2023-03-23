package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

public class NullableIntConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if ("null".equals(source))
            return null;

        return DefaultArgumentConverter.INSTANCE.convert(source, targetType);
    }
}
