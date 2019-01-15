package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#MENDELIAN} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class MendelianVariantController extends AbstractVariantController {

    // TODO - use completness validator for validation

    private static final Logger LOGGER = LoggerFactory.getLogger(MendelianVariantController.class);

    // ******************** FXML elements, injected by FXMLLoader ********************************** //
    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

    private BooleanBinding isCompleteBinding;

    @FXML
    private HBox variantHBox;

    @FXML
    private ComboBox<String> chromosomeComboBox;

    @FXML
    private TextField positionTextField;

    @FXML
    private TextField referenceTextField;

    @FXML
    private TextField alternateTextField;

    @FXML
    private TextField snippetTextField;

    @FXML
    private ComboBox<Genotype> genotypeComboBox;

    @FXML
    private ComboBox<String> variantClassComboBox;

    @FXML
    private ComboBox<String> pathomechanismComboBox;

    @FXML
    private TextField regulatorTextField;

    @FXML
    private HBox validationHBox;

    @FXML
    private ComboBox<String> reporterComboBox;

    @FXML
    private TextField residualActivityTextField;

    @FXML
    private ComboBox<String> emsaComboBox;

    @FXML
    private TextField emsaTFSymbolTextField;

    @FXML
    private TextField emsaGeneIDTextField;

    @FXML
    private ComboBox<String> cosegregationComboBox;

    @FXML
    private ComboBox<String> comparabilityComboBox;

    @FXML
    private ComboBox<String> otherChoicesComboBox;

    @FXML
    private ComboBox<String> otherEffectComboBox;

    // ******************** FXML elements ********************************** //


    /**
     * Create instance of this class which acts as a controller from MVC pattern. Given the fact that this class extends
     * {@link javafx.scene.control.TitledPane} it can be managed by {@link DataController}.
     */
    @Inject
    public MendelianVariantController(GuiElementValues elementValues) {
        super(elementValues);
    }


    public void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        positionTextField.setTextFormatter(makeTextFormatter(positionTextField, VARIANT_POSITION_REGEXP));
        referenceTextField.setTextFormatter(makeTextFormatter(referenceTextField, ALLELE_REGEXP));
        alternateTextField.setTextFormatter(makeTextFormatter(alternateTextField, ALLELE_REGEXP));
        snippetTextField.setTextFormatter(makeTextFormatter(snippetTextField, SNIPPET_REGEXP));
        genotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        variantClassComboBox.getItems().addAll(elementValues.getVariantClass());
        pathomechanismComboBox.getItems().addAll(elementValues.getPathomechanism());
        reporterComboBox.getItems().addAll(elementValues.getReporter());
        emsaComboBox.getItems().addAll(elementValues.getEmsa());
        cosegregationComboBox.getItems().addAll(elementValues.getCosegregation());
        comparabilityComboBox.getItems().addAll(elementValues.getComparability());
        otherChoicesComboBox.getItems().addAll(elementValues.getOtherChoices());
        otherEffectComboBox.getItems().addAll(elementValues.getOtherEffect());

        // Create tooltips here
        decorateWithTooltip(positionTextField, "Genomic position of variant in 1-based (VCF style) numbering");
        decorateWithTooltip(referenceTextField, "Representation of reference allele in VCF style (see help)");
        decorateWithTooltip(alternateTextField, "Representation of alternate allele in VCF style (see help)");
        decorateWithTooltip(snippetTextField, "Snippet of nucleotide sequence near variant, e.g. 'ACGT[A/C]ACTG'");

        // value of the ComboBox is null if user did not click on anything, TextField contains empty string
        isCompleteBinding = Bindings.createBooleanBinding(() -> chromosomeComboBox.getValue() != null &&
                        positionTextField.getText() != null && positionTextField.getText().matches(VARIANT_POSITION_REGEXP) &&
                        referenceTextField.getText() != null && referenceTextField.getText().matches(ALLELE_REGEXP) &&
                        alternateTextField.getText() != null && alternateTextField.getText().matches(ALLELE_REGEXP) &&
                        snippetTextField.getText() != null && snippetTextField.getText().matches(SNIPPET_REGEXP) &&
                        genotypeComboBox.getValue() != null,
                chromosomeComboBox.valueProperty(), positionTextField.textProperty(), referenceTextField.textProperty(),
                alternateTextField.textProperty(), snippetTextField.textProperty(), genotypeComboBox.valueProperty());
    }

    @Override
    public void presentVariant(Variant variant) {
        if (variant.getVariantValidation().getContext().equals(VariantValidation.Context.MENDELIAN)) {
            // Variant
            genomeBuildComboBox.setValue(variant.getVariantPosition().getGenomeAssembly());
            chromosomeComboBox.setValue(variant.getVariantPosition().getContig().isEmpty() ? null : variant.getVariantPosition().getContig());
            positionTextField.setText(String.valueOf(variant.getVariantPosition().getPos()));
            referenceTextField.setText(variant.getVariantPosition().getRefAllele());
            alternateTextField.setText(variant.getVariantPosition().getAltAllele());
            snippetTextField.setText(variant.getSnippet());
            genotypeComboBox.setValue(variant.getGenotype());
            variantClassComboBox.setValue(variant.getVariantClass().isEmpty() ? null : variant.getVariantClass());
            pathomechanismComboBox.setValue(variant.getPathomechanism().isEmpty() ? null : variant.getPathomechanism());

            // Validation
            VariantValidation validation = variant.getVariantValidation();
            regulatorTextField.setText(validation.getRegulator());
            reporterComboBox.setValue(validation.getReporterRegulation().isEmpty() ? null : validation.getReporterRegulation());
            residualActivityTextField.setText(validation.getReporterResidualActivity());
            emsaComboBox.setValue(validation.getEmsaValidationPerformed() ? "yes" : "no");
            emsaTFSymbolTextField.setText(validation.getEmsaTfSymbol());
            emsaGeneIDTextField.setText(validation.getEmsaGeneId());
            cosegregationComboBox.setValue(validation.getCosegregation() ? "yes" : "no");
            comparabilityComboBox.setValue(validation.getComparability() ? "yes" : "no");
            otherChoicesComboBox.setValue(validation.getOtherChoices().isEmpty() ? null : validation.getOtherChoices());
            otherEffectComboBox.setValue(validation.getOtherEffect().isEmpty() ? null : validation.getOtherEffect());
        }
    }


    @Override
    public Variant getVariant() {
        if (isCompleteBinding.get()) {
            return Variant.newBuilder()
                    .setVariantPosition(VariantPosition.newBuilder()
                            .setGenomeAssembly(genomeBuildComboBox.getValue())
                            .setContig(chromosomeComboBox.getValue())
                            .setPos(Integer.parseInt(positionTextField.getText()))
                            .setRefAllele(referenceTextField.getText())
                            .setAltAllele(alternateTextField.getText())
                            .build())
                    .setSnippet(snippetTextField.getText())
                    .setGenotype(genotypeComboBox.getValue())
                    .setVariantClass(variantClassComboBox.getValue() == null ? "" : variantClassComboBox.getValue())
                    .setPathomechanism(pathomechanismComboBox.getValue() == null ? "" : pathomechanismComboBox.getValue())
                    // VALIDATION
                    .setVariantValidation(VariantValidation.newBuilder()
                            .setContext(VariantValidation.Context.MENDELIAN)
                            .setRegulator(regulatorTextField.getText())
                            .setReporterRegulation(reporterComboBox.getValue() == null ? "" : reporterComboBox.getValue())
                            .setReporterResidualActivity(residualActivityTextField.getText())
                            .setEmsaValidationPerformed(emsaComboBox.getValue() != null && emsaComboBox.getValue().equals("yes"))
                            .setEmsaTfSymbol(emsaTFSymbolTextField.getText())
                            .setEmsaGeneId(emsaGeneIDTextField.getText())
                            .setCosegregation(cosegregationComboBox.getValue() != null && cosegregationComboBox.getValue().equals("yes"))
                            .setComparability(comparabilityComboBox.getValue() != null && comparabilityComboBox.getValue().equals("yes"))
                            .setOtherChoices(otherChoicesComboBox.getValue() == null ? "" : otherChoicesComboBox.getValue())
                            .setOtherEffect(otherEffectComboBox.getValue() == null ? "" : otherEffectComboBox.getValue())
                            .build())
                    .build();
        } else {
            return Variant.getDefaultInstance();
        }


    }

    @Override
    public ObservableBooleanValue isCompleteBinding() {
        return isCompleteBinding;
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isCompleteBinding.get()) {
                        return String.format("Mendelian variant: %s %s:%s%s>%s", genomeBuildComboBox.getValue(),
                                chromosomeComboBox.getValue(), positionTextField.getText(), referenceTextField.getText(),
                                alternateTextField.getText());
                    } else {
                        return "Mendelian variant: INCOMPLETE";
                    }
                },
                genomeBuildComboBox.valueProperty(), chromosomeComboBox.valueProperty(), positionTextField.textProperty(),
                referenceTextField.textProperty(), alternateTextField.textProperty(), snippetTextField.textProperty(),
                genotypeComboBox.valueProperty());
    }
}
