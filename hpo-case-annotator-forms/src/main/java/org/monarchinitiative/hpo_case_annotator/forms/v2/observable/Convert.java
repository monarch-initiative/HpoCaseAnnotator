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
                toAge(member.getAge()),
                member.getSex());
    }

    private static DiseaseStatus toDiseaseStatus(ObservableDiseaseStatus status) {
        return DiseaseStatus.of(status.getDiseaseId(), status.getDiseaseName(), status.isExcluded());
    }

    private static PhenotypicFeature toPhenotypicFeature(ObservablePhenotypicFeature feature) {
        return PhenotypicFeature.of(feature.getTermId(), feature.isExcluded(), toAgeRange(feature.getObservationAge()));
    }

    private static AgeRange toAgeRange(ObservableAgeRange observableAgeRange) {
        return AgeRange.of(toAge(observableAgeRange.getOnset()), toAge(observableAgeRange.getResolution()));
    }

    private static Period toAge(ObservableAge age) {
        return Period.of(age.getYears(), age.getMonths(), age.getDays());
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
                toAge(individual.getAge()),
                individual.getSex());
    }

    /* ***************************************** To observable ****************************************************** */

    public static Optional<? extends ObservableStudy> toObservableStudy(Study study) {
        if (study instanceof FamilyStudy fs) {
            ObservableFamilyStudy familyStudy = new ObservableFamilyStudy();
            toObservableFamilyStudy(fs, familyStudy);
            return Optional.of(familyStudy);
        } else if (study instanceof CohortStudy cs) {
            ObservableCohortStudy cohortStudy = new ObservableCohortStudy();
            toObservableCohortStudy(cs, cohortStudy);
            return Optional.of(cohortStudy);
        } else {
            return Optional.empty();
        }
    }

    private static void toObservableCohortStudy(CohortStudy cs, ObservableCohortStudy study) {
        study.setId(cs.id());

        toObservablePublication(cs.publication(), study.getPublication());
        study.variants().addAll(cs.variants());
        cs.members()
                .map(Convert::toObservableIndividual)
                .forEachOrdered(oi -> study.members().add(oi));
        toObservableStudyMetadata(cs.studyMetadata(), study.getStudyMetadata());
    }

    public static <T extends FamilyStudy> void toObservableFamilyStudy(T familyStudy, ObservableFamilyStudy study) {
        study.setId(familyStudy.id());
        toObservablePublication(familyStudy.publication(), study.getPublication());
        study.variants().addAll(familyStudy.variants());

        toObservablePedigree(familyStudy.pedigree(), study.getPedigree());
        toObservableStudyMetadata(familyStudy.studyMetadata(), study.getStudyMetadata());
    }

    private static void toObservablePublication(Publication publication, ObservablePublication op) {
        op.authors().addAll(publication.authors());
        op.setTitle(publication.title());
        op.setJournal(publication.journal());
        op.setYear(publication.year());
        op.setVolume(publication.volume());
        op.setPages(publication.pages());
        op.setPmid(publication.pmid());
    }

    private static void toObservablePedigree(Pedigree pedigree, ObservablePedigree op) {
        pedigree.members()
                .map(Convert::toObservablePedigreeMember)
                .forEachOrdered(opm -> op.members().add(opm));
    }

    private static <T extends Individual, U extends BaseObservableIndividual.Builder<U>> U toBaseObservableIndividual(T individual, U builder) {
        return builder.setId(individual.id())
                .setYears(individual.age().map(Period::getYears).orElse(null))
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

    public static <T extends PedigreeMember> ObservablePedigreeMember toObservablePedigreeMember(T pedigreeMember) {
        return toBaseObservableIndividual(pedigreeMember, ObservablePedigreeMember.builder())
                .setParentalId(pedigreeMember.paternalId().orElse(null))
                .setMaternalId(pedigreeMember.maternalId().orElse(null))
                .setProband(pedigreeMember.isProband())
                .build();
    }

    public static <T extends Individual> ObservableIndividual toObservableIndividual(T individual) {
        return toBaseObservableIndividual(individual, ObservableIndividual.builder())
                .build();
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
        toObservableAgeRange(phenotypicFeature.observationAge(), opf.getObservationAge());

        return opf;
    }

    private static void toObservableAgeRange(AgeRange ageRange, ObservableAgeRange oar) {
        toObservableAge(ageRange.onset(), oar.getOnset());
        toObservableAge(ageRange.resolution(), oar.getResolution());
    }

    private static void toObservableAge(Period period, ObservableAge age) {
        age.setYears(period.getYears());
        age.setMonths(period.getMonths());
        age.setDays(period.getDays());
    }

    private static void toObservableStudyMetadata(StudyMetadata studyMetadata, ObservableStudyMetadata osm) {
        osm.setFreeText(studyMetadata.freeText());
        osm.setCreatedBy(studyMetadata.createdBy());
        osm.modifiedBy().addAll(studyMetadata.modifiedBy());
    }
}
