package org.monarchinitiative.hpo_case_annotator.convert;

/**
 * Classes implementing this interface are able to convert instances of {@link A} to format {@link B}, and back.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public interface Codec<A, B> {

    B encode(A data) throws ModelTransformationException;

    A decode(B data) throws ModelTransformationException;

}
