package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.v2.AgeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.DiseaseTableController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualVariantSummaryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.VariantAwareBindingController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class BaseIndividualController<T extends BaseObservableIndividual> extends VariantAwareBindingController<T> {

    private final ToggleGroup sexToggleGroup = new ToggleGroup();

    @FXML
    private TextField individualIdTextField;
    @FXML
    private VBox age;
    @FXML
    private AgeController ageController;
    @FXML
    private RadioButton maleSexRadioButton;
    @FXML
    private RadioButton femaleSexRadioButton;
    @FXML
    private RadioButton unknownSexRadioButton;
    @FXML
    private Label observedFeatureCountLabel;
    @FXML
    private Label notObservedFeatureCountLabel;
    @FXML
    private VBox diseaseTable;
    @FXML
    private DiseaseTableController diseaseTableController;
    @FXML
    private VBox individualVariantSummary;
    @FXML
    private IndividualVariantSummaryController individualVariantSummaryController;

    private ListChangeListener<ObservablePhenotypicFeature> summarizePhenotypeCount;
    private ObjectBinding<Sex> sexBinding;


    @FXML
    protected void initialize() {
        super.initialize();

        maleSexRadioButton.setToggleGroup(sexToggleGroup);
        femaleSexRadioButton.setToggleGroup(sexToggleGroup);
        unknownSexRadioButton.setToggleGroup(sexToggleGroup);

        summarizePhenotypeCount = prepareSummarizePhenotypeCountListener();
        sexBinding = prepareSexBinding();
    }

    private ListChangeListener<ObservablePhenotypicFeature> prepareSummarizePhenotypeCountListener() {
        return change -> {
            while (change.next()) {
                summarizePhenotypeCount(change.getList());
            }
        };
    }

    private void summarizePhenotypeCount(List<? extends ObservablePhenotypicFeature> phenotypicFeatures) {
        Map<Boolean, List<ObservablePhenotypicFeature>> phenotypesByPresence = phenotypicFeatures.stream()
                .collect(Collectors.partitioningBy(ObservablePhenotypicFeature::isExcluded));

        int nPresentTerms = phenotypesByPresence.getOrDefault(false, List.of()).size();
        String presentTermsMessage = nPresentTerms == 1
                ? "1 present phenotype term"
                : String.format("%d present phenotype terms", nPresentTerms);
        observedFeatureCountLabel.setText(presentTermsMessage);

        int nAbsentTerms = phenotypesByPresence.getOrDefault(true, List.of()).size();
        String absentTermsMessage = nAbsentTerms == 1
                ? "1 absent phenotype term"
                : String.format("%d absent phenotype terms", nAbsentTerms);
        notObservedFeatureCountLabel.setText(absentTermsMessage);
    }

    private ObjectBinding<Sex> prepareSexBinding() {
        return Bindings.createObjectBinding(() -> {
                    RadioButton selectedToggle = (RadioButton) sexToggleGroup.getSelectedToggle();
                    if (selectedToggle == null) return null;
                    if (selectedToggle.equals(maleSexRadioButton)) {
                        return Sex.MALE;
                    } else if (selectedToggle.equals(femaleSexRadioButton)) {
                        return Sex.FEMALE;
                    } else {
                        return Sex.UNKNOWN;
                    }
                },
                sexToggleGroup.selectedToggleProperty());
    }

    /* ************************************************************************************************************** */

    @Override
    public ObservableList<CuratedVariant> curatedVariants() {
        return individualVariantSummaryController.variants();
    }

    @Override
    protected void bind(T individual) {
        individualIdTextField.textProperty().bindBidirectional(individual.idProperty());

        // age
        // TODO - fix or discard
//        ageController.dataProperty().bindBidirectional(individual.observableAgeProperty());

        // sex
        if (individual.getSex() != null) {
            switch (individual.getSex()) {
                case MALE -> sexToggleGroup.selectToggle(maleSexRadioButton);
                case FEMALE -> sexToggleGroup.selectToggle(femaleSexRadioButton);
                default -> sexToggleGroup.selectToggle(unknownSexRadioButton);
            }
        }

        individual.sexProperty().bind(sexBinding);

        // phenotype
        summarizePhenotypeCount(individual.phenotypicFeaturesProperty());
        individual.phenotypicFeaturesProperty().addListener(summarizePhenotypeCount);

        // diseases
        Bindings.bindContentBidirectional(diseaseTableController.diseaseStatuses(), individual.diseaseStatesProperty());

        // genotypes
        Bindings.bindContentBidirectional(individualVariantSummaryController.genotypesList(), individual.getGenotypes());
    }

    @Override
    protected void unbind(T individual) {
        individualIdTextField.textProperty().unbindBidirectional(individual.idProperty());

        // age
        // TODO - fix or discard
//        ageController.dataProperty().unbindBidirectional(individual.observableAgeProperty());

        // sex
        individual.sexProperty().unbind();

        // phenotype
        individual.phenotypicFeaturesProperty().removeListener(summarizePhenotypeCount);

        // diseases
        Bindings.unbindContentBidirectional(diseaseTableController.diseaseStatuses(), individual.diseaseStatesProperty());

        // genotypes
        Bindings.unbindContentBidirectional(individualVariantSummaryController.genotypes(), individual.getGenotypes());
    }

}
