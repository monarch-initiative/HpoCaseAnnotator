/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.monarchinitiative.hpo_case_annotator.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.HPO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version X.Y.Z
 * @since X.Y
 */
public class JSONHpoDeserializer extends JsonDeserializer<List<HPO>> {

    @Override
    public List<HPO> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        System.err.println(String.format("HPOs: '%s'", p.getText()));
        return new ArrayList<>();
    }
}
