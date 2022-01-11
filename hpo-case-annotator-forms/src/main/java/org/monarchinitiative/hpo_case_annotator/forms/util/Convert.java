package org.monarchinitiative.hpo_case_annotator.forms.util;

import org.monarchinitiative.hpo_case_annotator.forms.observable.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservableDiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;

import java.time.Period;

public class Convert {

    private Convert() {
    }

    public static <T extends ObservablePedigreeMember> PedigreeMember toPedigreeMember(T member) {
        return PedigreeMember.of(member.getId(),
                nullWhenEmptyOrBlankInput(member.getPaternalId()),
                nullWhenEmptyOrBlankInput(member.getMaternalId()),
                member.isProband(),
                member.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
                member.diseaseStatuses().stream().map(Convert::toDiseaseStatus).toList(),
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

    public static <T extends ObservableIndividual> Individual toIndividual(T individual) {
        return Individual.of(individual.getId(),
                individual.phenotypicFeatures().stream().map(Convert::toPhenotypicFeature).toList(),
                individual.diseaseStatuses().stream().map(Convert::toDiseaseStatus).toList(),
                individual.genotypes(),
                individual.getAge(),
                individual.getSex());
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

    public static <T extends Individual> ObservableIndividual toObservableIndividual(T individual) {
        return toBaseObservableIndividual(individual, ObservableIndividual.builder()).build();
    }
}
