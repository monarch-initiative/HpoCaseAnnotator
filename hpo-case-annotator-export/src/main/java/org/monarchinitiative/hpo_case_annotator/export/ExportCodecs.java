package org.monarchinitiative.hpo_case_annotator.export;

import com.google.protobuf.Message;
import org.monarchinitiative.hpo_case_annotator.model.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;

import java.util.HashMap;
import java.util.Map;

/**
 * Static utility class with factory methods that provide {@link Codec}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class ExportCodecs {

    private ExportCodecs() {
        // private no-op
    }

    public static Codec<DiseaseCase, Message> diseaseCasePhenopacketCodec() {
        return DiseaseCaseToV1PhenopacketCodec.instance();
    }

    public static Codec<Study, Message> familyStudyToFamilyCodec() {
        return StudyToV2PhenopacketCodec.instance();
    }

    /**
     * Biocurated data can be represented in file in these formats.
     */
    public enum SupportedDiseaseCaseFormat {

        /**
         * The format represented by {@link DiseaseCase} class (protobuf).
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
            regexMap.put(SupportedDiseaseCaseFormat.JSON, "[\\w\\W]+\\.json");
            return Map.copyOf(regexMap);
        }
    }
}
