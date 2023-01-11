package org.monarchinitiative.hpo_case_annotator.export.ppv2;

import com.google.protobuf.Message;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.phenopackets.schema.v2.Cohort;

public class CohortStudyToV2PhenopacketCodec extends BaseStudyToV2PhenopacketCodec<CohortStudy, Cohort> {

    public CohortStudyToV2PhenopacketCodec(Ontology hpo) {
        super(hpo);
    }

    private static Message encodeCohortStudy(CohortStudy cohort) throws ModelTransformationException {
//        return Cohort.newBuilder()
//                .setId(cohort.getId())
//                .setDescription(cohort.getStudyMetadata().getFreeText())
//                .addAllMembers(cohort.getMembers().stream()
//                        .map(individualToPhenopacket())
//                        .toList())
//                .setMetaData(encodeMetaData(cohort.getPublication(), cohort.getStudyMetadata()))
//                .build();
        // TODO(ielis) - implement
        return null;
    }

    @Override
    public Cohort encode(CohortStudy data) throws ModelTransformationException {
        // TODO - implement.
        // We need to figure out how to create interpretation based on genotypes of the cohort members.
        throw new ModelTransformationException("NOT YET IMPLEMENTED!");
    }

}
