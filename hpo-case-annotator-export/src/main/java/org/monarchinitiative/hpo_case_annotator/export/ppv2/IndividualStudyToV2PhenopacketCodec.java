package org.monarchinitiative.hpo_case_annotator.export.ppv2;

import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.GenomicVariant;
import org.monarchinitiative.svart.Strand;
import org.phenopackets.phenopackettools.builder.PhenopacketBuilder;
import org.phenopackets.phenopackettools.builder.builders.*;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.phenopackets.phenopackettools.builder.builders.OntologyClassBuilder.ontologyClass;

/**
 * <h2>Assumptions</h2>
 *
 * The codec currently makes several assumptions:
 * <ul>
 *     <li>the interpretation progress status is assumed SOLVED</li>
 *     <li>the genomic interpretation status is assumed UNKNOWN</li>
 *     <li>a random UUID can be assigned to the interpretation ID</li>
 * </ul>
 * <p>
 *
 * <h2>Limitations</h2>
 *
 * The genotype/allelic state is not exported for variants with {@code HOM_REF} genotype. Only one diagnosis is allowed.
 */
public class IndividualStudyToV2PhenopacketCodec extends BaseStudyToV2PhenopacketCodec<IndividualStudy, Phenopacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualStudyToV2PhenopacketCodec.class);

    public IndividualStudyToV2PhenopacketCodec(Ontology hpo) {
        super(hpo);
    }

    @Override
    public Phenopacket encode(IndividualStudy study) throws ModelTransformationException {
        MetaData metaData = encodeMetaData(study.getPublication(), study.getStudyMetadata());
        PhenopacketBuilder builder = PhenopacketBuilder.create(study.getId(), metaData);

        Map<String, ? extends CuratedVariant> variantByMd5Hex = study.getVariants().stream()
                .collect(Collectors.toMap(CuratedVariant::md5Hex, Function.identity()));

        encodeIndividual(study.getIndividual(), variantByMd5Hex, builder);

        return builder.build();
    }

    private void encodeIndividual(Individual individual,
                                         Map<String, ? extends CuratedVariant> variantByMd5Hex,
                                         PhenopacketBuilder phenopacketBuilder) throws ModelTransformationException {
        // INDIVIDUAL
        // - id
        IndividualBuilder individualBuilder = IndividualBuilder.builder(individual.getId())
                .homoSapiens();

        // - age
        mapTimeElement(individual.getAge())
                .ifPresentOrElse(
                        individualBuilder::ageAtLastEncounter,
                        () -> LOGGER.warn("Unable to map age of {}", individual.getId())
                );

        // - vital status
        VitalStatus vitalStatus = individual.getVitalStatus();
        switch (individual.getVitalStatus().getStatus()) {
            case UNKNOWN -> { /* No-op, unknown is already the default value. */ }
            case ALIVE -> individualBuilder.alive();
            case DECEASED -> {
                VitalStatusBuilder builder = VitalStatusBuilder.deceased();
                mapTimeElement(vitalStatus.getTimeOfDeath())
                        .ifPresent(builder::timeOfDeath);
                individualBuilder.vitalStatus(builder.build());
            }
        }

        // - sex
        switch (individual.getSex()) {
            case UNKNOWN -> individualBuilder.unknownSex();
            case MALE -> individualBuilder.male();
            case FEMALE -> individualBuilder.female();
        }
        phenopacketBuilder.individual(individualBuilder.build());


        // PHENOTYPIC FEATURES
        individual.getPhenotypicFeatures().stream()
                .flatMap(this::mapToPhenotypicFeature)
                .forEachOrdered(phenopacketBuilder::addPhenotypicFeature);


        // DISEASE STATES
        individual.getDiseaseStates().stream()
                .flatMap(IndividualStudyToV2PhenopacketCodec::mapToDisease)
                .forEachOrdered(phenopacketBuilder::addDisease);

        // VARIANT GENOTYPES -> INTERPRETATIONs
        List<? extends DiseaseStatus> presentDiseaseStates = individual.getDiseaseStates().stream()
                .filter(DiseaseStatus::isPresent)
                .toList();
        if (!individual.getGenotypes().isEmpty() && presentDiseaseStates.size() != 1)
            // We only export variants if the number of disease states is 1.
            throw new ModelTransformationException("Export of a study with >1 variants for a study with >1 observed diseases is not yet supported");


        DiseaseStatus diseaseStatus = presentDiseaseStates.get(0);
        DiagnosisBuilder diagnosis = DiagnosisBuilder.builder(diseaseStatus.getDiseaseId().id().getValue(), diseaseStatus.getDiseaseId().getDiseaseName());

        List<? extends VariantGenotype> genotypes = individual.getGenotypes();
        for (int i = 0; i < genotypes.size(); i++) {
            VariantGenotype vgt = genotypes.get(i);
            CuratedVariant cv = variantByMd5Hex.get(vgt.getMd5Hex());
            if (cv == null) {
                LOGGER.warn("Unable to export genotype of variant[{}]", i);
                continue;
            }
            Optional<GenomicVariant> gvo = cv.getVariant();
            if (gvo.isEmpty()) {
                LOGGER.warn("Unable to export variant[{}], cannot create representation", i);
                continue;
            }

            VariationDescriptorBuilder vdb = VariationDescriptorBuilder.builder(cv.id().orElse(cv.md5Hex()));
            GenomicVariant gv = gvo.get();

            // The builder creation handles the sequence notation, nothing really to add here.
            VcfRecordBuilder builder = VcfRecordBuilder.builder(cv.getGenomicAssembly(),
                    gv.contigName(),
                    gv.startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()),
                    gv.ref(),
                    gv.alt())
                    .id(gv.id());
            if (gv.isSymbolic() && !gv.isBreakend()) {
                // But we may need to add some extra bits in case of symbolic variant.
                builder.info(
                        "SVTYPE=%s;END=%d".formatted(
                                gv.variantType().baseType().toString(),
                                gv.endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))
                );
            }

            vdb.vcfRecord(builder.build());

            //
            switch (vgt.getGenotype()) {
                // TODO(ielis) - replace with builder method once we use pxf>=1.0.0-RC2
                case UNKNOWN, UNSET -> vdb.zygosity(ontologyClass("GENO:0000137", "unspecified zygosity"));
                case HETEROZYGOUS -> vdb.heterozygous();
                case HOMOZYGOUS_ALTERNATE -> vdb.homozygous();
                case HOMOZYGOUS_REFERENCE -> {
                    // TODO - what we want here?
                }
                case HEMIZYGOUS -> vdb.hemizygous();
            }

            VariantInterpretation vi = VariantInterpretationBuilder.builder(vdb)
                    .actionabilityUnknown()
                    .acmgNotProvided()
                    .build();

            GenomicInterpretationBuilder giBuilder = GenomicInterpretationBuilder.builder(individual.getId())
                    .unknown() // TODO - what genomic interpretation status do we want here? We should NOT just assume.. :'(
                    .variantInterpretation(vi);
            diagnosis.addGenomicInterpretation(giBuilder.build());
        }

        UUID interpretationId = UUID.randomUUID();
        InterpretationBuilder builder = InterpretationBuilder.builder(interpretationId.toString());
        // TODO - what interpretation status should we use? We should not just assume solved.
        phenopacketBuilder.addInterpretation(builder.solved(diagnosis.build()));
    }

}
