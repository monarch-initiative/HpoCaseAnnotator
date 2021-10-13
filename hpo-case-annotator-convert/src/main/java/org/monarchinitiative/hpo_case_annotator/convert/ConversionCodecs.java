package org.monarchinitiative.hpo_case_annotator.convert;


import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;

/**
 * Static utility class with factory methods that provide {@link Codec}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class ConversionCodecs {

    private ConversionCodecs() {
        // private no-op
    }

    public static Codec<DiseaseCase, Study> diseaseCaseStudyCodec() {
        return V1toV2Codec.getInstance();
    }


}
