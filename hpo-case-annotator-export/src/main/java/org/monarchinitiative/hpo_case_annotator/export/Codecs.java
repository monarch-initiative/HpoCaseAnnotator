package org.monarchinitiative.hpo_case_annotator.export;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.hpo_case_annotator.io.Checks;
import org.monarchinitiative.hpo_case_annotator.io.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.io.v1.ProtoJSONModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.transform.Codec;
import org.monarchinitiative.hpo_case_annotator.model.transform.V1toV2Codec;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.phenopackets.schema.v1.Phenopacket;

import java.util.*;

/**
 * Static utility class with factory methods that provide {@link Codec}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Codecs {

    private Codecs() {
        // private no-op
    }

    public static Codec<DiseaseCase, Phenopacket> diseaseCasePhenopacketCodec() {
        return new DiseaseCaseToPhenopacketCodec();
    }

    public static Codec<DiseaseCase, Study> diseaseCaseStudyCodec() {
        return V1toV2Codec.getInstance();
    }


    static String getPhenopacketIdFor(DiseaseCase model) {
        /*
        Make sure the phenopacket ID can be used as a valid path (no chars like / & < > | etc.)
         */
        Publication publication = model.getPublication();
        String firstAuthorsSurname = ModelUtils.getFirstAuthorsSurname(publication.getAuthorList());

        String year = (publication.getYear().isEmpty()) ? "no_year" : publication.getYear();
        String pmid = (publication.getPmid().isEmpty()) ? "no_pmid" : publication.getPmid();

        Gene gene = model.getGene();
        String geneSymbol = (gene.getSymbol().isEmpty()) ? "no_gene" : gene.getSymbol();

        String familyProband = model.getFamilyInfo().getFamilyOrProbandId();
        String probandId = familyProband.isEmpty() ? "no_proband_id" : familyProband;

        List<String> tokens = new LinkedList<>();
        for (String item : Arrays.asList(firstAuthorsSurname, year, pmid, geneSymbol, probandId)) {
            String noDash = item.replaceAll("-", "_");
            String normalized = ModelUtils.normalizeAsciiText(noDash);
            String legal = Checks.makeLegalFileNameWithNoWhitespace(normalized, '_');
            tokens.add(legal);
        }

        return String.join("-", tokens);
    }

    /**
     * Biocurated data can be represented in file in these formats.
     */
    public enum SupportedDiseaseCaseFormat {

        /**
         * The format represented by {@link DiseaseCase} class (protobuf) and decoded by {@link ProtoJSONModelParser}.
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
            return ImmutableMap.copyOf(regexMap);
        }
    }
}
