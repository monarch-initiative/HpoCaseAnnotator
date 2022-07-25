package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.time.Period;
import java.util.Optional;

public class Convert {

    // TODO -

    private Convert() {
    }

//    public static Optional<Study> toStudy(ObservableStudy<?> study) {
//        if (study instanceof ObservableFamilyStudy fs) {
//            return Optional.of(toFamilyStudy(fs));
//        } else if (study instanceof ObservableCohortStudy cs) {
//            return Optional.of(toCohortStudy(cs));
//        } else {
//            return Optional.empty();
//        }
//    }

//    public static FamilyStudy toFamilyStudy(ObservableFamilyStudy study) {
//        return FamilyStudy.of(study.getId(),
//                toPublication(study.getPublication()),
//                study.variants(),
//                toPedigree(study.getObservablePedigree()),
//                toStudyMetadata(study.getStudyMetadata()));
//    }
//
//    public static Publication toPublication(ObservablePublication publication) {
//        return Publication.of(publication.getAuthors(),
//                publication.getTitle(),
//                publication.getJournal(),
//                publication.getYear(),
//                publication.getVolume(),
//                publication.getPages(),
//                publication.getPmid());
//    }

//    private static Pedigree toPedigree(ObservablePedigree pedigree) {
//        return Pedigree.of(pedigree.membersList().stream().map(Convert::toPedigreeMember).toList());
//    }

//    public static <T extends ObservablePedigreeMember> PedigreeMember toPedigreeMember(T member) {
//        return PedigreeMember.of(member.getId(),
//                nullWhenEmptyOrBlankInput(member.getPaternalId().filter(s -> !s.isBlank()).orElse(null)),
//                nullWhenEmptyOrBlankInput(member.getMaternalId().filter(s -> !s.isBlank()).orElse(null)),
//                member.isProband(),
//                member.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
//                member.diseaseStatuses().stream().map(Convert::toDiseaseStatus).toList(),
//                member.getGenotypes(),
////                toAge(member.getObservableAge()),
//                null,
//                member.getSex());
//    }

//    public static DiseaseStatus toDiseaseStatus(ObservableDiseaseStatus status) {
////        return DiseaseStatus.of(toDiseaseIdentifier(status.getDiseaseIdentifier()), status.isExcluded());
//        return null;
//    }

//    public static DiseaseIdentifier toDiseaseIdentifier(ObservableDiseaseIdentifier odi) {
//        return DiseaseIdentifier.of(odi.id(), odi.getDiseaseName());
//    }

//    private static PhenotypicFeature toPhenotypicFeature(ObservablePhenotypicFeature feature) {
//        return PhenotypicFeature.of(feature.getTermId(), feature.isExcluded(), toAgeRange(feature.getObservationAge()));
//    }
//
//    private static AgeRange toAgeRange(ObservableAgeRange observableAgeRange) {
//        return AgeRange.of(observableAgeRange.getOnset(), observableAgeRange.getResolution());
//    }
//
//    private static String nullWhenEmptyOrBlankInput(String value) {
//        return value == null || value.isBlank() || value.isEmpty()
//                ? null
//                : value;
//    }
//
//    private static StudyMetadata toStudyMetadata(ObservableStudyMetadata studyMetadata) {
//        return StudyMetadata.of(studyMetadata.getFreeText(), studyMetadata.getCreatedBy(), studyMetadata.getModifiedBy());
//    }

//    public static CohortStudy toCohortStudy(ObservableCohortStudy study) {
//        return CohortStudy.of(study.getId(),
//                toPublication(study.getPublication()),
//                study.variants(),
//                study.observableMembers().stream().map(Convert::toIndividual).toList(),
//                toStudyMetadata(study.getStudyMetadata()));
//    }

//    public static <T extends ObservableIndividual> Individual toIndividual(T individual) {
//        return Individual.of(individual.getId(),
//                individual.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
//                individual.diseaseStatuses().stream().map(Convert::toDiseaseStatus).toList(),
//                individual.getGenotypes(),
////                toAge(individual.getObservableAge()),
//                null,
//                individual.getSex());
//    }

    /* ***************************************** To observable ****************************************************** */

//    public static Optional<? extends ObservableStudy> toObservableStudy(Study study) {
//        if (study instanceof FamilyStudy fs) {
//            ObservableFamilyStudy familyStudy = new ObservableFamilyStudy();
//            toObservableFamilyStudy(fs, familyStudy);
//            return Optional.of(familyStudy);
//        } else if (study instanceof CohortStudy cs) {
//            ObservableCohortStudy cohortStudy = new ObservableCohortStudy();
//            toObservableCohortStudy(cs, cohortStudy);
//            return Optional.of(cohortStudy);
//        } else {
//            return Optional.empty();
//        }
//    }

//    private static void toObservableCohortStudy(CohortStudy cs, ObservableCohortStudy study) {
//        study.setId(cs.getId());
//
//        toObservablePublication(cs.getPublication(), study.getPublication());
//        study.variants().addAll(cs.getVariants());
//        cs.getMembers().stream()
//                .map(Convert::toObservableIndividual)
//                .forEachOrdered(oi -> study.observableMembers().add(oi));
//        toObservableStudyMetadata(cs.getStudyMetadata(), study.getStudyMetadata());
//    }

//    public static <T extends FamilyStudy> void toObservableFamilyStudy(T familyStudy, ObservableFamilyStudy study) {
//        study.setId(familyStudy.getId());
//        toObservablePublication(familyStudy.getPublication(), study.getPublication());
//        study.variants().addAll(familyStudy.getVariants());
//
//        toObservablePedigree(familyStudy.getPedigree(), study.getObservablePedigree());
//        toObservableStudyMetadata(familyStudy.getStudyMetadata(), study.getStudyMetadata());
//    }

//    public static void toObservablePublication(Publication publication, ObservablePublication op) {
//        op.setAuthors(String.join(", ", publication.getAuthors()));
//        op.setTitle(publication.getTitle());
//        op.setJournal(publication.getJournal());
//        op.setYear(publication.getYear());
//        op.setVolume(publication.getVolume());
//        op.setPages(publication.getPages());
//        op.setPmid(publication.getPmid());
//    }

//    private static void toObservablePedigree(Pedigree pedigree, ObservablePedigree op) {
//        pedigree.getMembers().stream()
//                .map(Convert::toObservablePedigreeMember)
//                .forEachOrdered(opm -> op.membersList().add(opm));
//    }

//    private static <T extends Individual, U extends BaseObservableIndividual.Builder<U>> U toBaseObservableIndividual(T individual, U builder) {
//        return builder.setId(individual.getId())
//                .setYears(individual.getAge().map(Age::getYears).orElse(null))
//                .setMonths(individual.getAge().map(Age::getMonths).orElse(0))
//                .setDays(individual.getAge().map(Age::getDays).orElse(0))
//                .setSex(individual.getSex())
//                .addAllPhenotypicFeatures(individual.getPhenotypicFeatures().stream()
//                        .map(Convert::toObservablePhenotypicFeature)
//                        .toList())
//                .addAllDiseaseStatuses(individual.getDiseases().stream()
//                        .map(Convert::toObservableDiseaseStatus)
//                        .toList())
//                .putAllGenotypes(individual.getGenotypes());
//    }

//    public static <T extends PedigreeMember> ObservablePedigreeMember toObservablePedigreeMember(T pedigreeMember) {
//        return toBaseObservableIndividual(pedigreeMember, ObservablePedigreeMember.builder())
//                .setParentalId(pedigreeMember.getPaternalId().orElse(null))
//                .setMaternalId(pedigreeMember.getMaternalId().orElse(null))
//                .setProband(pedigreeMember.isProband())
//                .build();
//    }

//    public static <T extends Individual> ObservableIndividual toObservableIndividual(T individual) {
//        return toBaseObservableIndividual(individual, ObservableIndividual.builder())
//                .build();
//    }

//    private static ObservableDiseaseStatus toObservableDiseaseStatus(DiseaseStatus diseaseStatus) {
//        ObservableDiseaseStatus ods = new ObservableDiseaseStatus();
//
////        toObservableDiseaseIdentifier(diseaseStatus.getDiseaseId(), ods.getDiseaseIdentifier());
//        ods.setExcluded(diseaseStatus.isExcluded());
//
//        return ods;
//    }

//    public static void toObservableDiseaseIdentifier(DiseaseIdentifier diseaseIdentifier, ObservableDiseaseIdentifier odi) {
//        odi.setDiseaseId(diseaseIdentifier.id());
//        odi.setDiseaseName(diseaseIdentifier.getDiseaseName());
//    }
//
//    public static ObservablePhenotypicFeature toObservablePhenotypicFeature(PhenotypicFeature phenotypicFeature) {
//        ObservablePhenotypicFeature opf = new ObservablePhenotypicFeature();
//
//        opf.setTermId(phenotypicFeature.id());
//        opf.setExcluded(phenotypicFeature.isExcluded());
//        toObservableAgeRange(phenotypicFeature.getObservationAge(), opf.getObservationAge());
//
//        return opf;
//    }

//    private static void toObservableAgeRange(AgeRange ageRange, ObservableAgeRange oar) {
////        toObservableAge(ageRange.getOnset(), oar.getOnset());
////        toObservableAge(ageRange.getResolution(), oar.getResolution());
//    }
//
//    private static void toObservableAge(Period period, ObservableAge age) {
//        age.setYears(period.getYears());
//        age.setMonths(period.getMonths());
//        age.setDays(period.getDays());
//    }
//
//    private static void toObservableStudyMetadata(StudyMetadata studyMetadata, ObservableStudyMetadata osm) {
//        osm.setFreeText(studyMetadata.getFreeText());
////        osm.setCreatedBy(studyMetadata.getCreatedBy());
////        osm.getModifiedBy().addAll(studyMetadata.getModifiedBy());
//    }
}
