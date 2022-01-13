package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import org.monarchinitiative.hpo_case_annotator.model.v2.*;

import java.time.Period;
import java.util.Optional;

public class Convert {

    private Convert() {
    }

    public static Optional<Study> toStudy(ObservableStudy study) {
        if (study instanceof ObservableFamilyStudy fs) {
            return Optional.of(toFamilyStudy(fs));
        } else if (study instanceof ObservableCohortStudy cs) {
            return Optional.of(toCohortStudy(cs));
        } else {
            return Optional.empty();
        }
    }

    public static FamilyStudy toFamilyStudy(ObservableFamilyStudy study) {
        return FamilyStudy.of(study.getId(),
                toPublication(study.getPublication()),
                study.variants(),
                toPedigree(study.getPedigree()),
                toStudyMetadata(study.getStudyMetadata()));
    }

    private static Publication toPublication(ObservablePublication publication) {
        return Publication.of(publication.authors(),
                publication.getTitle(),
                publication.getJournal(),
                publication.getYear(),
                publication.getVolume(),
                publication.getPages(),
                publication.getPmid());
    }

    private static Pedigree toPedigree(ObservablePedigree pedigree) {
        return Pedigree.of(pedigree.members().stream().map(Convert::toPedigreeMember).toList());
    }

    public static <T extends ObservablePedigreeMember> PedigreeMember toPedigreeMember(T member) {
        return PedigreeMember.of(member.getId(),
                nullWhenEmptyOrBlankInput(member.getPaternalId()),
                nullWhenEmptyOrBlankInput(member.getMaternalId()),
                member.isProband(),
                member.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
                member.diseaseStates().stream().map(Convert::toDiseaseStatus).toList(),
                member.genotypes(),
                member.getAge(),
                member.getSex());
    }

    private static DiseaseStatus toDiseaseStatus(ObservableDiseaseStatus status) {
        return DiseaseStatus.of(status.getDiseaseId(), status.getDiseaseName(), status.isExcluded());
    }

    private static PhenotypicFeature toPhenotypicFeature(ObservablePhenotypicFeature feature) {
        return PhenotypicFeature.of(feature.getTermId(), feature.isExcluded(), toAgeRange(feature.getObservationAge()));
    }

    private static AgeRange toAgeRange(ObservableAgeRange observableAgeRange) {
        return AgeRange.of(observableAgeRange.getOnset(), observableAgeRange.getResolution());
    }

    private static String nullWhenEmptyOrBlankInput(String value) {
        return value == null || value.isBlank() || value.isEmpty()
                ? null
                : value;
    }

    private static StudyMetadata toStudyMetadata(ObservableStudyMetadata studyMetadata) {
        return StudyMetadata.of(studyMetadata.getFreeText(), studyMetadata.getCreatedBy(), studyMetadata.modifiedBy());
    }

    public static CohortStudy toCohortStudy(ObservableCohortStudy study) {
        return CohortStudy.of(study.getId(),
                toPublication(study.getPublication()),
                study.variants(),
                study.members().stream().map(Convert::toIndividual).toList(),
                toStudyMetadata(study.getStudyMetadata()));
    }

    public static <T extends ObservableIndividual> Individual toIndividual(T individual) {
        return Individual.of(individual.getId(),
                individual.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
                individual.diseaseStates().stream().map(Convert::toDiseaseStatus).toList(),
                individual.genotypes(),
                individual.getAge(),
                individual.getSex());
    }

    /* ***************************************** To observable ****************************************************** */

    public static Optional<? extends ObservableStudy> toObservableStudy(Study study) {
        if (study instanceof FamilyStudy fs) {
            return Optional.of(toObservableFamilyStudy(fs));
        } else if (study instanceof CohortStudy cs) {
            return Optional.of(toObservableCohortStudy(cs));
        } else {
            return Optional.empty();
        }
    }

    private static ObservableCohortStudy toObservableCohortStudy(CohortStudy cs) {
        ObservableCohortStudy ocs = new ObservableCohortStudy();

        ocs.setId(cs.id());
        ocs.setPublication(toObservablePublication(cs.publication()));
        ocs.variants().addAll(cs.variants());
        cs.members()
                .map(Convert::toObservableIndividual)
                .forEachOrdered(oi -> ocs.members().add(oi));
        ocs.setStudyMetadata(toObservableStudyMetadata(cs.studyMetadata()));

        return ocs;
    }

    public static <T extends FamilyStudy> ObservableFamilyStudy toObservableFamilyStudy(T familyStudy) {
        ObservableFamilyStudy study = new ObservableFamilyStudy();

        study.setId(familyStudy.id());
        study.setPublication(toObservablePublication(familyStudy.publication()));
        study.variants().addAll(familyStudy.variants());
        study.setPedigree(toObservablePedigree(familyStudy.pedigree()));
        study.setStudyMetadata(toObservableStudyMetadata(familyStudy.studyMetadata()));

        return study;
    }

    private static ObservablePublication toObservablePublication(Publication publication) {
        ObservablePublication op = new ObservablePublication();

        op.authors().addAll(publication.authors());
        op.setTitle(publication.title());
        op.setJournal(publication.journal());
        op.setYear(publication.year());
        op.setVolume(publication.volume());
        op.setPages(publication.pages());
        op.setPmid(publication.pmid());

        return op;
    }

    private static ObservablePedigree toObservablePedigree(Pedigree pedigree) {
        ObservablePedigree op = new ObservablePedigree();

        pedigree.members()
                .map(Convert::toObservablePedigreeMember)
                .forEachOrdered(opm -> op.members().add(opm));

        return op;
    }

    public static <T extends PedigreeMember> ObservablePedigreeMember toObservablePedigreeMember(T pedigreeMember) {
        return toBaseObservableIndividual(pedigreeMember, ObservablePedigreeMember.builder())
                .setParentalId(pedigreeMember.paternalId().orElse(null))
                .setMaternalId(pedigreeMember.maternalId().orElse(null))
                .setProband(pedigreeMember.isProband())
                .build();
    }

    private static <T extends Individual, U extends BaseObservableIndividual.Builder<U>> U toBaseObservableIndividual(T individual, U builder) {
        return builder.setId(individual.id())
                .setYears(individual.age().map(p -> String.valueOf(p.getYears())).orElse(null))
                .setMonths(individual.age().map(Period::getMonths).orElse(0))
                .setDays(individual.age().map(Period::getDays).orElse(0))
                .setSex(individual.sex())
                .addAllPhenotypicFeatures(individual.phenotypicFeatures()
                        .map(Convert::toObservablePhenotypicFeature)
                        .toList())
                .addAllDiseaseStatuses(individual.diseases().stream()
                        .map(Convert::toObservableDiseaseStatus)
                        .toList())
                .putAllGenotypes(individual.genotypes());
    }

    private static ObservableDiseaseStatus toObservableDiseaseStatus(DiseaseStatus diseaseStatus) {
        ObservableDiseaseStatus ods = new ObservableDiseaseStatus();

        ods.setDiseaseId(diseaseStatus.diseaseId());
        ods.setDiseaseName(diseaseStatus.diseaseName());
        ods.setExcluded(diseaseStatus.isExcluded());

        return ods;
    }

    public static ObservablePhenotypicFeature toObservablePhenotypicFeature(PhenotypicFeature phenotypicFeature) {
        ObservablePhenotypicFeature opf = new ObservablePhenotypicFeature();

        opf.setTermId(phenotypicFeature.termId());
        opf.setExcluded(phenotypicFeature.isExcluded());
        opf.setObservationAge(toObservableAgeRange(phenotypicFeature.observationAge()));

        return opf;
    }

    private static ObservableAgeRange toObservableAgeRange(AgeRange ageRange) {
        ObservableAgeRange oar = new ObservableAgeRange();

        oar.setOnset(ageRange.onset());
        oar.setResolution(ageRange.resolution());

        return oar;
    }

    private static ObservableStudyMetadata toObservableStudyMetadata(StudyMetadata studyMetadata) {
        ObservableStudyMetadata osm = new ObservableStudyMetadata();

        osm.setFreeText(studyMetadata.freeText());
        osm.setCreatedBy(studyMetadata.createdBy());
        osm.modifiedBy().addAll(studyMetadata.modifiedBy());

        return osm;
    }

    public static <T extends Individual> ObservableIndividual toObservableIndividual(T individual) {
        return toBaseObservableIndividual(individual, ObservableIndividual.builder())
                .build();
    }
}
