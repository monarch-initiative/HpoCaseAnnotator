package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.*;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#SOMATIC} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SomaticVariantController extends AbstractVariantController {


    // ******************** FXML elements, injected by FXMLLoader ********************************** //
    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

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
    private TextField reporterResidualActivityTextField;

    @FXML
    private ComboBox<String> emsaComboBox;

    @FXML
    private TextField emsaTFSymbolTextField;

    @FXML
    private TextField emsaGeneIDTextField;

    @FXML
    private TextField cancerNTextField;

    @FXML
    private TextField cancerMTextField;

    @FXML
    private ComboBox<String> otherChoicesComboBox;

    @FXML
    private ComboBox<String> otherEffectComboBox;
    // ****************** end of FXML elements ***************************** //


    /**
     * Create instance of this class which acts as a controller from MVC pattern.
     */
    @Inject
    public SomaticVariantController(GuiElementValues elementValues) {
        super(elementValues);
    }


    public void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        // add text formatter to impose constraint on the field
        positionTextField.setTextFormatter(makeTextFormatter(positionTextField, POSITIVE_INTEGER_REGEXP));
        referenceTextField.setTextFormatter(makeTextFormatter(referenceTextField, ALLELE_REGEXP));
        alternateTextField.setTextFormatter(makeTextFormatter(alternateTextField, ALLELE_REGEXP));
        snippetTextField.setTextFormatter(makeTextFormatter(snippetTextField, SNIPPET_REGEXP));
        genotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        variantClassComboBox.getItems().addAll(elementValues.getVariantClass());
        pathomechanismComboBox.getItems().addAll(elementValues.getPathomechanism());
        reporterComboBox.getItems().addAll(elementValues.getReporter());
        emsaComboBox.getItems().addAll(elementValues.getEmsa());
        otherChoicesComboBox.getItems().addAll(elementValues.getOtherChoices());
        otherEffectComboBox.getItems().addAll(elementValues.getOtherEffect());

        cancerMTextField.setTextFormatter(makeToleratingTextFormatter(cancerMTextField, POSITIVE_INTEGER_REGEXP));
        cancerNTextField.setTextFormatter(makeToleratingTextFormatter(cancerNTextField, POSITIVE_INTEGER_REGEXP));

        // Create tooltips here
        decorateWithTooltip(positionTextField, "Genomic position of the first nucleotide of REF allele \n(positive integer, 1-based numbering)");
        decorateWithTooltip(referenceTextField, "Representation of reference allele in VCF style");
        decorateWithTooltip(alternateTextField, "Representation of alternate allele in VCF style");
        decorateWithTooltip(snippetTextField, "Snippet of nucleotide sequence near variant, e.g. 'ACGT[A/C]ACTG'");
        decorateWithTooltip(cancerMTextField, "Positive integer");
        decorateWithTooltip(cancerNTextField, "Positive integer");
    }


    @Override
    public void presentData(Variant variant) {
        // Variant
        genomeBuildComboBox.setValue(variant.getVariantPosition().getGenomeAssembly());
        chromosomeComboBox.setValue(variant.getVariantPosition().getContig());
        // do not set zero, but rather an empty string
        positionTextField.setText(variant.getVariantPosition().getPos() == 0 ? "" : String.valueOf(variant.getVariantPosition()));
        referenceTextField.setText(variant.getVariantPosition().getRefAllele());
        alternateTextField.setText(variant.getVariantPosition().getAltAllele());
        snippetTextField.setText(variant.getSnippet());
        genotypeComboBox.setValue(variant.getGenotype());
        variantClassComboBox.setValue(variant.getVariantClass());
        pathomechanismComboBox.setValue(variant.getPathomechanism());

        // Validation
        VariantValidation validation = variant.getVariantValidation();
        regulatorTextField.setText(validation.getRegulator());
        reporterComboBox.setValue(validation.getReporterRegulation());
        emsaComboBox.setValue(validation.getEmsaValidationPerformed() ? "yes" : "no");
        emsaTFSymbolTextField.setText(validation.getEmsaTfSymbol());
        emsaGeneIDTextField.setText(validation.getEmsaGeneId());
        otherChoicesComboBox.setValue(validation.getOtherChoices());
        otherEffectComboBox.setValue(validation.getOtherEffect());

        // do not set zero, but rather an empty string
        cancerMTextField.setText(validation.getMPatients() == 0 ? "" : String.valueOf(validation.getMPatients()));
        cancerNTextField.setText(validation.getNPatients() == 0 ? "" : String.valueOf(validation.getNPatients()));
    }


    @Override
    public Variant getData() {
        int pos;
        try {
            pos = Integer.parseInt(positionTextField.getText());
        } catch (NumberFormatException nfe) {
            pos = 0;
        }

        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(genomeBuildComboBox.getValue() == null ? GenomeAssembly.NOT_KNOWN : genomeBuildComboBox.getValue())
                        .setContig(chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue())
                        .setPos(pos)
                        .setRefAllele(referenceTextField.getText())
                        .setAltAllele(alternateTextField.getText())
                        .build())
                .setSnippet(snippetTextField.getText())
                .setGenotype(genotypeComboBox.getValue() == null ? Genotype.UNKNOWN_GENOTYPE : genotypeComboBox.getValue())
                .setVariantClass(variantClassComboBox.getValue() == null ? "" : variantClassComboBox.getValue())
                .setPathomechanism(pathomechanismComboBox.getValue() == null ? "" : pathomechanismComboBox.getValue())
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SOMATIC)
                        .setRegulator(regulatorTextField.getText())
                        .setReporterRegulation(reporterComboBox.getValue() == null ? "" : reporterComboBox.getValue())
                        .setEmsaValidationPerformed(emsaComboBox.getValue() != null && emsaComboBox.getValue().equals("yes"))
                        .setEmsaTfSymbol(emsaTFSymbolTextField.getText())
                        .setEmsaGeneId(emsaGeneIDTextField.getText())
                        .setOtherChoices(otherChoicesComboBox.getValue() == null ? "" : otherChoicesComboBox.getValue())
                        .setOtherEffect(otherEffectComboBox.getValue() == null ? "" : otherEffectComboBox.getValue())
                        .setMPatients(Integer.parseInt(cancerMTextField.getText().isEmpty() ? "0" : cancerMTextField.getText()))
                        .setNPatients(Integer.parseInt(cancerNTextField.getText().isEmpty() ? "0" : cancerNTextField.getText()))
                        .build())
                .build();
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Somatic variant: %s %s:%s%s>%s", genomeBuildComboBox.getValue(),
                                chromosomeComboBox.getValue(), positionTextField.getText(), referenceTextField.getText(),
                                alternateTextField.getText());
                    } else {
                        return "Somatic variant: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0])
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(genomeBuildComboBox.valueProperty(), chromosomeComboBox.valueProperty(), positionTextField.textProperty(),
                referenceTextField.textProperty(), alternateTextField.textProperty(), snippetTextField.textProperty(),
                genotypeComboBox.valueProperty());
    }
}
