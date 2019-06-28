package org.monarchinitiative.hpo_case_annotator.model.codecs;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.DiseaseCaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Static utility class with factory methods that provide {@link Codec}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Codecs {

    private Codecs() {
        // private no-op
    }

    public static DiseaseCaseToPhenopacketCodec diseaseCasePhenopacketCodec() {
        return new DiseaseCaseToPhenopacketCodec();
    }

    public static DiseaseCaseToDiseaseCaseModelCodec diseaseCaseToDiseaseCaseModelCodec() {
        return new DiseaseCaseToDiseaseCaseModelCodec();
    }

    public static DiseaseCaseToThreesPhenopacketCodec threesPhenopacketCodec() {
        return new DiseaseCaseToThreesPhenopacketCodec();
    }


    /**
     * Biocurated data can be represented in file in these formats.
     */
    public enum SupportedDiseaseCaseFormat {

        /**
         * The format represented by {@link DiseaseCaseModel} class and decoded by {@link org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser}.
         */
        XML,

        /**
         * The format represented by {@link DiseaseCase} class (protobuf) and decoded by {@link org.monarchinitiative.hpo_case_annotator.model.io.ProtoJSONModelParser}.
         */
        JSON;


        private static final Map<SupportedDiseaseCaseFormat, String> REGEX_MAP = initializeRegexMap();

        /**
         * Immutable map where keys are all possible enum values. The map values are regexp strings. The name of a file
         * containing {@link DiseaseCase} data must conform to this regex in order to be represented by the
         * {@link SupportedDiseaseCaseFormat} value.
         */
        public static Map<SupportedDiseaseCaseFormat, String> getRegexMap() {
            return REGEX_MAP;
        }

        private static Map<SupportedDiseaseCaseFormat, String> initializeRegexMap() {
            Map<SupportedDiseaseCaseFormat, String> regexMap = new HashMap<>();
            regexMap.put(SupportedDiseaseCaseFormat.XML, "[\\w\\W]+\\.xml");
            regexMap.put(SupportedDiseaseCaseFormat.JSON, "[\\w\\W]+\\.json");
            return ImmutableMap.copyOf(regexMap);
        }
    }
}
