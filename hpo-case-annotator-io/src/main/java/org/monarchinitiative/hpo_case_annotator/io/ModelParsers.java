package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.io.v1.ProtoJSONModelParser;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.JsonStudyParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;

public class ModelParsers {

    private ModelParsers() {
        // private no-op
    }

    public static final class V1 {
        public static ModelParser<DiseaseCase> jsonParser() {
            return ProtoJSONModelParser.getInstance();
        }
    }
    public static final class V2 {
        public static ModelParser<Study> jsonParser() {
            return JsonStudyParser.getInstance();
        }

    }

}
