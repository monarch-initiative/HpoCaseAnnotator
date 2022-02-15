package org.monarchinitiative.hpo_case_annotator.model.convert;

/**
 * Classes implementing this interface are able to convert instances of {@link SOURCE} to {@link TARGET} format.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @see BidirectionalCodec for a codec capable of two-way conversion.
 */
public interface Codec<SOURCE, TARGET> {

    TARGET encode(SOURCE data) throws ModelTransformationException;

}
