package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableDiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class BaseIndividualController<T extends BaseObservableIndividual> extends BindingDataController<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndividualController.class);

    private final ToggleGroup sexToggleGroup = new ToggleGroup();

    @FXML
    private TextField individualIdTextField;
    @FXML
    private TextField yearsTextField;
    @FXML
    private ComboBox<Integer> monthsComboBox;
    @FXML
    private ComboBox<Integer> daysComboBox;
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
//    @FXML
//    private Button addEditPhenotypeFeaturesButton;
    @FXML
    private ListView<ObservableDiseaseStatus> diseaseListView;
//    @FXML
//    private Button addEditDiseaseButton;
    @FXML
    private VBox individualVariantSummary;
    @FXML
    private IndividualVariantSummaryController individualVariantSummaryController;

    private ListChangeListener<ObservablePhenotypicFeature> summarizePhenotypeCount;
    private ObjectBinding<Sex> sexBinding;


    @FXML
    protected void initialize() {
        super.initialize();
        yearsTextField.setTextFormatter(Formats.numberFormatter());
        monthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        monthsComboBox.getSelectionModel().selectFirst();
        daysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        daysComboBox.getSelectionModel().selectFirst();

        maleSexRadioButton.setToggleGroup(sexToggleGroup);
        femaleSexRadioButton.setToggleGroup(sexToggleGroup);
        unknownSexRadioButton.setToggleGroup(sexToggleGroup);

        diseaseListView.setCellFactory(ObservableDiseaseStatusListCell.of());

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
                : String.format("%d present phenotype terms", nAbsentTerms);
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

    public ObservableList<CuratedVariant> variants() {
        return individualVariantSummaryController.variants();
    }

    @Override
    protected void bind(T individual) {
        individualIdTextField.textProperty().bindBidirectional(individual.idProperty());

        // age
        yearsTextField.textProperty().bindBidirectional(individual.yearsProperty());
        monthsComboBox.valueProperty().bindBidirectional(individual.monthsProperty());
        daysComboBox.valueProperty().bindBidirectional(individual.daysProperty());

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
        summarizePhenotypeCount(individual.phenotypicFeatures());
        individual.phenotypicFeatures().addListener(summarizePhenotypeCount);

        // diseases
        Bindings.bindContentBidirectional(diseaseListView.getItems(), individual.diseaseStates());

        // genotypes
        Bindings.bindContentBidirectional(individualVariantSummaryController.genotypes(), individual.genotypes());
    }

    @Override
    protected void unbind(T individual) {
        individualIdTextField.textProperty().unbindBidirectional(individual.idProperty());

        // age
        yearsTextField.textProperty().unbindBidirectional(individual.yearsProperty());
        monthsComboBox.valueProperty().unbindBidirectional(individual.monthsProperty());
        daysComboBox.valueProperty().unbindBidirectional(individual.daysProperty());

        // sex
        individual.sexProperty().unbind();

        // phenotype
        individual.phenotypicFeatures().removeListener(summarizePhenotypeCount);

        // diseases
        Bindings.unbindContentBidirectional(diseaseListView.getItems(), individual.diseaseStates());

        // genotypes
        Bindings.unbindContentBidirectional(individualVariantSummaryController.genotypes(), individual.genotypes());
    }

/*
    TODO - deal with phenotypes
    @FXML
    private void addEditPhenotypeFeaturesButtonAction() {

    }

    @FXML
    private void addEditDiseaseButtonAction() {
        // TODO - show disease browser
        LOGGER.info("Adding/editing diseases");
    }
    */
}
