package org.monarchinitiative.hpo_case_annotator.model.convert;

/**
 * Classes implementing this interface are able to convert instances of {@link SOURCE} to {@link TARGET} format, and back.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public interface Codec<SOURCE, TARGET> {

    TARGET encode(SOURCE data) throws ModelTransformationException;

}
