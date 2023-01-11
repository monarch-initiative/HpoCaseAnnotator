package org.monarchinitiative.hpo_case_annotator.export.ppv2;

import com.google.protobuf.Message;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.phenopackets.phenopackettools.builder.FamilyBuilder;
import org.phenopackets.phenopackettools.builder.PhenopacketBuilder;
import org.phenopackets.phenopackettools.builder.builders.IndividualBuilder;
import org.phenopackets.schema.v2.Family;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.MetaData;

import java.util.List;

public class FamilyStudyToV2PhenopacketCodec extends BaseStudyToV2PhenopacketCodec<FamilyStudy, Family> {

    public FamilyStudyToV2PhenopacketCodec(Ontology hpo) {
        super(hpo);
    }

    @Override
    public Family encode(FamilyStudy data) throws ModelTransformationException {
        // TODO - implement. There are some function fragments in the class but the utility must be re-assessed.
        throw new ModelTransformationException("NOT YET IMPLEMENTED!");
    }


    private static Phenopacket mapToPhenopacket(PedigreeMember proband, MetaData metaData) {
        // TODO - implement
        IndividualBuilder individual = IndividualBuilder.builder(proband.getId())
                .homoSapiens();
        // sex - TODO - karyotypic sex
        individual = switch (proband.getSex()) {
            case MALE -> individual.male();
            case FEMALE -> individual.female();
            case UNKNOWN -> individual.unknownSex();
        };



        // TODO - age at last encounter, vital status
        return PhenopacketBuilder.create(proband.getId(), metaData)
                .individual(individual
                        .build())
                .build();
    }

    private static PedigreeMember findProband(Pedigree pedigree) throws ModelTransformationException {
        List<? extends PedigreeMember> probands = pedigree.getMembers().stream()
                .filter(PedigreeMember::isProband)
                .toList();
        if (probands.size() != 1)
            throw new ModelTransformationException("There must be exactly 1 proband in the family but found %d".formatted(probands.size()));

        return probands.get(0);
    }

    private Message encodeFamilyStudy(FamilyStudy family) throws ModelTransformationException {
        // TODO - implement
        PedigreeMember proband = findProband(family.getPedigree());
        MetaData metaData = encodeMetaData(family.getPublication(), family.getStudyMetadata());
        return FamilyBuilder.create(family.getId())
                .phenopacket(mapToPhenopacket(proband, metaData))
                .metaData(metaData)
                .build();
        // TODO - implement. We need to figure out how to create interpretation based on genotypes of the cohort members.
        //  Specifically, HCA does not keep track of interpretation status,  for the variant, we do not know ho
    }
}
