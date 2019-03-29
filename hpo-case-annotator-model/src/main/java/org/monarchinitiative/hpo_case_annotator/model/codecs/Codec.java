package org.monarchinitiative.hpo_case_annotator.model.codecs;

import java.util.function.Function;

/**
 * Classes implementing this interface are able to convert instances of {@link A} to format {@link B}, and vice versa.
 * <p>
 * Classes should specify if <code>encode</code> and <code>decode</code> are <em>lossy</em> or <em>lossless</em> conversion.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public interface Codec<A, B> {

    B encode(A data);

    A decode(B data);


    default Function<A, B> encodeFunction() {
        return this::encode;
    }

    default Function<B, A> decodeFunction() {
        return this::decode;
    }
}
