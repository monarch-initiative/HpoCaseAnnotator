package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.core.io.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#SOMATIC} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SomaticVariantController extends AbstractVariantController {

    // ******************** FXML elements, injected by FXMLLoader ********************************** //

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
     * Create instance of this class which acts as a controller from MVC pattern. Given the fact that this class extends
     * {@link javafx.scene.control.TitledPane} it can be managed by {@link DataController}.
     */
    public SomaticVariantController(ChoiceBasket choiceBasket) throws IOException {
        super(choiceBasket);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("SomaticVariantView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        this.getStylesheets().add(getClass().getResource("VariantView.css").toExternalForm());
        loader.load();

        populateContent();
    }


    /**
     * Read yaml configuration file and initialize content of fxml view elements. Add tooltips
     */
    @Override
    protected void populateContent() {
        this.setText(VariantValidation.Context.SOMATIC.toString());

        chromosomeComboBox.getItems().addAll(choiceBasket.getChromosome());
        genotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        variantClassComboBox.getItems().addAll(choiceBasket.getVariantClass());
        pathomechanismComboBox.getItems().addAll(choiceBasket.getPathomechanism());
        reporterComboBox.getItems().addAll(choiceBasket.getReporter());
        emsaComboBox.getItems().addAll(choiceBasket.getEmsa());
        otherChoicesComboBox.getItems().addAll(choiceBasket.getOtherChoices());
        otherEffectComboBox.getItems().addAll(choiceBasket.getOtherEffect());

        addTooltip(positionTextField, "Genomic position of variant in 1-based (VCF style) numbering");
        addTooltip(referenceTextField, "Representation of reference allele in VCF style (see help)");
        addTooltip(alternateTextField, "Representation of alternate allele in VCF style (see help)");
    }


    @Override
    public void presentVariant(Variant variant) {
        // Variant
        chromosomeComboBox.setValue(variant.getContig());
        positionTextField.setText(String.valueOf(variant.getPos()));
        referenceTextField.setText(variant.getRefAllele());
        alternateTextField.setText(variant.getAltAllele());
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

        cancerMTextField.setText(String.valueOf(validation.getMPatients()));
        cancerNTextField.setText(String.valueOf(validation.getNPatients()));
    }


    @Override
    public Variant getVariant() {
        return Variant.newBuilder()
                .setContig(chromosomeComboBox.getValue())
                .setPos(Integer.parseInt(positionTextField.getText())) // TODO - make sure that the int is an int here
                .setRefAllele(referenceTextField.getText())
                .setAltAllele(alternateTextField.getText())
                .setSnippet(snippetTextField.getText())
                .setGenotype(genotypeComboBox.getValue())
                .setVariantClass(variantClassComboBox.getValue())
                .setPathomechanism(pathomechanismComboBox.getValue())
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SOMATIC)
                        .setRegulator(regulatorTextField.getText())
                        .setReporterRegulation(reporterComboBox.getValue())
                        .setEmsaValidationPerformed(emsaComboBox.getValue().equals("yes"))
                        .setEmsaTfSymbol(emsaTFSymbolTextField.getText())
                        .setEmsaGeneId(emsaGeneIDTextField.getText())
                        .setOtherChoices(otherChoicesComboBox.getValue())
                        .setOtherEffect(otherEffectComboBox.getValue())
                        .setMPatients(Integer.parseInt(cancerMTextField.getText())) // TODO - make sure that the int is an int here
                        .setNPatients(Integer.parseInt(cancerNTextField.getText())) // TODO - make sure that the int is an int here
                        .build())
                .build();

    }
}
