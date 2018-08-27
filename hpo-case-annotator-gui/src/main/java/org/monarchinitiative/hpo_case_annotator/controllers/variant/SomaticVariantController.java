package org.monarchinitiative.hpo_case_annotator.controllers.variant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.model.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.model.SomaticVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import java.io.IOException;

/**
 * Controller class responsible for presenting instances of {@link SomaticVariant} model class. Acts as a controller
 * in MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SomaticVariantController extends BaseVariantController {

    /**
     * Instance of class which acts as a Model component of MVC pattern and its properties are bound to view elements
     */
    private final SomaticVariant variant;

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
    private ComboBox<String> genotypeComboBox;

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
     *
     * @param variant instance of {@link SomaticVariant} model object.
     */
    public SomaticVariantController(SomaticVariant variant, ChoiceBasket choiceBasket) throws IOException {
        super(choiceBasket);
        this.variant = variant;


        FXMLLoader loader = new FXMLLoader(getClass().getResource("SomaticVariantView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        this.getStylesheets().add(getClass().getResource("VariantView.css").toExternalForm());
        loader.load();

        populateContent();
        initializeBindings();

    }


    @Override
    public Variant getVariant() {
        return variant;
    }


    /**
     * Read yaml configuration file and initialize content of fxml view elements. Add tooltips
     */
    @Override
    protected void populateContent() {
        chromosomeComboBox.getItems().addAll(choiceBasket.getChromosome());
        genotypeComboBox.getItems().addAll(choiceBasket.getGenotype());
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


    /**
     * Create bindings of FXML view elements with model fields to ensure proper synchronization between model & view content
     */
    @Override
    protected void initializeBindings() {
        // Variant
        chromosomeComboBox.valueProperty().bindBidirectional(variant.chromosomeProperty());
        positionTextField.textProperty().bindBidirectional(variant.positionProperty());
        referenceTextField.textProperty().bindBidirectional(variant.referenceAlleleProperty());
        alternateTextField.textProperty().bindBidirectional(variant.alternateAlleleProperty());
        snippetTextField.textProperty().bindBidirectional(variant.snippetProperty());
        genotypeComboBox.valueProperty().bindBidirectional(variant.genotypeProperty());
        variantClassComboBox.valueProperty().bindBidirectional(variant.variantClassProperty());
        pathomechanismComboBox.valueProperty().bindBidirectional(variant.pathomechanismProperty());
        regulatorTextField.textProperty().bindBidirectional(variant.regulatorProperty());
        // Validation
        reporterComboBox.valueProperty().bindBidirectional(variant.reporterRegulationProperty());
        reporterResidualActivityTextField.textProperty().bindBidirectional(variant.reporterResidualActivityProperty());
        emsaComboBox.valueProperty().bindBidirectional(variant.emsaValidationPerformedProperty());
        emsaTFSymbolTextField.textProperty().bindBidirectional(variant.emsaTFSymbolProperty());
        emsaGeneIDTextField.textProperty().bindBidirectional(variant.emsaGeneIdProperty());
        cancerNTextField.textProperty().bindBidirectional(variant.nPatientsProperty());
        cancerMTextField.textProperty().bindBidirectional(variant.mPatientsProperty());
        otherChoicesComboBox.valueProperty().bindBidirectional(variant.otherProperty());
        otherEffectComboBox.valueProperty().bindBidirectional(variant.otherEffectProperty());

        this.setText(variant.getVariantMode().name());
    }
}
