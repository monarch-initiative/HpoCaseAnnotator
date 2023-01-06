package org.monarchinitiative.hpo_case_annotator.convert;


import org.monarchinitiative.hpo_case_annotator.model.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.phenol.ontology.data.Ontology;

/**
 * Static utility class with factory methods that provide {@link Codec}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class ConversionCodecs {

    private ConversionCodecs() {
        // private no-op
    }

    public static Codec<DiseaseCase, Study> v1ToV2(Ontology ontology) {
        return V1toV2Codec.of(ontology);
    }


}
