package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class BaseIndividualController<T extends BaseObservableIndividual> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndividualController.class);
    private final ToggleGroup sexToggleGroup = new ToggleGroup();
    private final ObjectProperty<T> individual = new SimpleObjectProperty<>(this, "observableIndividual");
    @FXML
    protected TextField individualIdTextField;
    @FXML
    protected TextField yearsTextField;
    @FXML
    protected ComboBox<Integer> monthsComboBox;
    @FXML
    protected ComboBox<Integer> daysComboBox;
    @FXML
    protected RadioButton maleSexRadioButton;
    @FXML
    protected RadioButton femaleSexRadioButton;
    @FXML
    protected RadioButton unknownSexRadioButton;
    @FXML
    protected Label observedFeatureCountLabel;
    @FXML
    protected Label notObservedFeatureCountLabel;
    @FXML
    protected Button addEditPhenotypeFeaturesButton;
    @FXML
    protected ListView<ObservableDiseaseStatus> diseaseListView;
    @FXML
    protected Button addEditDiseaseButton;
    @FXML
    protected HBox individualVariantSummary;
    @FXML
    protected IndividualVariantSummaryController individualVariantSummaryController;

    private ListChangeListener<ObservablePhenotypicFeature> summarizePhenotypeCount;
    private ObjectBinding<Sex> sexBinding;


    @FXML
    protected void initialize() {
        yearsTextField.setTextFormatter(Formats.numberFormatter());
        monthsComboBox.getItems().addAll(FormUtils.getIntegers(11));
        monthsComboBox.getSelectionModel().selectFirst();
        daysComboBox.getItems().addAll(FormUtils.getIntegers(30));
        daysComboBox.getSelectionModel().selectFirst();

        maleSexRadioButton.setToggleGroup(sexToggleGroup);
        femaleSexRadioButton.setToggleGroup(sexToggleGroup);
        unknownSexRadioButton.setToggleGroup(sexToggleGroup);

        diseaseListView.setCellFactory(ObservableDiseaseStatusListCell.of());

        summarizePhenotypeCount = summarizePhenotypeCountListener();
        sexBinding = sexBinding();

        individual.addListener(individualChangeListener());
    }

    /*
     * Properties of the Individual
     */

    protected abstract ChangeListener<T> individualChangeListener();

    private ListChangeListener<ObservablePhenotypicFeature> summarizePhenotypeCountListener() {
        return change -> {
            while (change.next()) {
                summarizePhenotypeCount(change.getList());
            }
        };
    }

    protected void summarizePhenotypeCount(List<? extends ObservablePhenotypicFeature> phenotypicFeatures) {
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

    private ObjectBinding<Sex> sexBinding() {
        return Bindings.createObjectBinding(() -> {
            RadioButton selectedToggle = (RadioButton) sexToggleGroup.getSelectedToggle();
            if (selectedToggle.equals(maleSexRadioButton)) {
                return Sex.MALE;
            } else if (selectedToggle.equals(femaleSexRadioButton)) {
                return Sex.FEMALE;
            } else {
                return Sex.UNKNOWN;
            }
        }, sexToggleGroup.selectedToggleProperty());
    }

    /* ************************************************************************************************************** */

    public T getIndividual() {
        return individual.get();
    }

    public void setIndividual(T individual) {
        this.individual.set(individual);
    }

    public ObjectProperty<T> individualProperty() {
        return individual;
    }

    protected void bind(T novel) {
        individualIdTextField.textProperty().bindBidirectional(novel.idProperty());

        // age
        yearsTextField.textProperty().bindBidirectional(novel.yearsProperty());
        monthsComboBox.valueProperty().bindBidirectional(novel.monthsProperty());
        daysComboBox.valueProperty().bindBidirectional(novel.daysProperty());

        // sex
        switch (novel.getSex()) {
            case MALE -> sexToggleGroup.selectToggle(maleSexRadioButton);
            case FEMALE -> sexToggleGroup.selectToggle(femaleSexRadioButton);
            default -> sexToggleGroup.selectToggle(unknownSexRadioButton);
        }
        novel.sexProperty().bind(sexBinding);

        // phenotype
        summarizePhenotypeCount(novel.phenotypicFeatures());
        novel.phenotypicFeatures().addListener(summarizePhenotypeCount);

        // diseases
        Bindings.bindContent(diseaseListView.getItems(), novel.diseaseStatuses());

        // genotypes are dealt with
//        individualVariantSummaryController.genotypes();
    }

    protected void unbind(T old) {
        individualIdTextField.textProperty().unbindBidirectional(old.idProperty());

        // age
        yearsTextField.textProperty().unbindBidirectional(old.yearsProperty());
        monthsComboBox.valueProperty().unbindBidirectional(old.monthsProperty());
        daysComboBox.valueProperty().unbindBidirectional(old.daysProperty());

        // sex
        old.sexProperty().unbind();

        // phenotype
        old.phenotypicFeatures().removeListener(summarizePhenotypeCount);

        // diseases
        Bindings.unbindContent(diseaseListView.getItems(), old.diseaseStatuses());
    }

    public ListChangeListener<CuratedVariant> curatedVariantChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    // add the added variants into variantSummaryController and assign Genotype.HOMOZYGOUS_REFERENCE
                    for (CuratedVariant addedVariant : c.getAddedSubList()) {
                        boolean variantIsPresentInTheSummary = individualVariantSummaryController.genotypedVariants().stream()
                                .anyMatch(gv -> gv.curatedVariant().md5Hex().equals(addedVariant.md5Hex()));
                        if (!variantIsPresentInTheSummary)
                            individualVariantSummaryController.genotypedVariants().add(GenotypedVariant.of(addedVariant, Genotype.UNKNOWN));
                    }
                } else if (c.wasRemoved()) {
                    List<Integer> indices = new ArrayList<>(2);
                    for (CuratedVariant variantToRemove : c.getRemoved()) {
                        // find indices of the variantToRemove
                        int idx = 0;
                        for (GenotypedVariant gv : individualVariantSummaryController.genotypedVariants()) {
                            if (gv.curatedVariant().md5Hex().equals(variantToRemove.md5Hex()))
                                indices.add(idx);
                            idx++;
                        }

                        // remove variants at present indices
                        for (Integer index : indices) {
                            individualVariantSummaryController.genotypedVariants().remove((int) index);
                        }
                        indices.clear();
                    }
                } else //noinspection StatementWithEmptyBody
                    if (c.wasReplaced()) {
                        // We do not handle `c.wasReplaced()` since we handle `c.wasAdded()` and `c.wasRemoved()` (see Javadoc for `c.wasReplaced()`)
                    } else {
                        LOGGER.warn("Unexpected list change operation was performed: {}", c);
                    }
            }
        };
    }

    @FXML
    private void addEditPhenotypeFeaturesButtonAction() {
        // TODO - show phenotype browser
        LOGGER.info("Adding/editing phenotype features");
    }

    @FXML
    private void addEditDiseaseButtonAction() {
        // TODO - show disease browser
        LOGGER.info("Adding/editing diseases");
    }
}
