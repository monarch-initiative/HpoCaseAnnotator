package org.monarchinitiative.hpo_case_annotator.controller.variant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.controller.DataController;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import java.io.IOException;

/**
 * Controller class responsible for presenting instances of {@link SplicingVariant} model class. Acts as a controller
 * in MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SplicingVariantController extends BaseVariantController {

    /**
     * FXML file describing layout of "view". Acts as view in MVC pattern.
     */
    private static final String fxmlResource = "/org/monarchinitiative/hpo_case_annotator/controller/variant/SplicingVariantView.fxml";

    /**
     * Path to CSS file containing info for rendering all Variant "views".
     */
    private static final String CSSheet = "/css/VariantView.css";

    /**
     * Instance of class which acts as a Model component of MVC pattern and its properties are bound to view elements
     */
    private final SplicingVariant variant;

    // ******************** FXML elements, injected by FXMLLoader ********************************** //
    // ************************* Variant *********************************** //

    @FXML
    private ComboBox<String> varChromosomeComboBox;

    @FXML
    private TextField varPositionTextField;

    @FXML
    private TextField varReferenceTextField;

    @FXML
    private TextField varAlternateTextField;

    @FXML
    private TextField varSnippetTextField;

    @FXML
    private ComboBox<String> varGenotypeComboBox;

    @FXML
    private ComboBox<String> varClassComboBox;

    @FXML
    private ComboBox<String> varPathomechanismComboBox;

    @FXML
    private ComboBox<String> varConsequenceComboBox;

    @FXML
    private TextField crypticSpliceSitePositionTextField;

    @FXML
    private ComboBox<String> crypticSpliceSiteTypeComboBox;

    @FXML
    private TextField crypticSpliceSiteSnippetTextField;

    // ************************* Validation ******************************** //
    @FXML
    private CheckBox minigeneCheckBox;

    @FXML
    private CheckBox siteDirectedMutagenesisCheckBox;

    @FXML
    private CheckBox rtPCRCheckBox;

    @FXML
    private CheckBox cDNASeqencingCheckBox;

    @FXML
    private CheckBox pcrCheckBox;

    @FXML
    private CheckBox srProteinOverexpressionCheckBox;

    @FXML
    private CheckBox srProteinKnockdownCheckBox;

    @FXML
    private CheckBox mutationOfWTSpliceSiteCheckBox;

    @FXML
    private CheckBox otherCheckBox;

    @FXML
    private ComboBox<String> cosegregationComboBox;

    @FXML
    private ComboBox<String> comparabilityComboBox;


    /**
     * Create instance of this class which acts as a controller from MVC pattern. Given the fact that this class extends
     * {@link javafx.scene.control.TitledPane} it can be managed by {@link DataController}.
     *
     * @param variant instance of {@link SplicingVariant} model object.
     */
    public SplicingVariantController(SplicingVariant variant) {
        super();
        this.variant = variant;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResource));
            loader.setController(this);
            loader.setRoot(this);
            super.getStylesheets().add(getClass().getResource(CSSheet).toExternalForm());
            loader.load();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();
        }

        populateContent();
        initializeBindings();
    }


    @Override
    public Variant getVariant() {
        return variant;
    }


    /**
     * Read yaml configuration file and initialize content of fxml view elements. Create tooltips.
     */
    @Override
    protected void populateContent() {
        varChromosomeComboBox.getItems().addAll(basket.getChromosome());
        varGenotypeComboBox.getItems().addAll(basket.getGenotype());
        varClassComboBox.getItems().addAll(basket.getVariantClass());
        varPathomechanismComboBox.getItems().addAll(basket.getPathomechanism());
        varConsequenceComboBox.getItems().addAll(basket.getConsequence());
        crypticSpliceSiteTypeComboBox.getItems().addAll(basket.getCrypticSpliceSiteType());
        cosegregationComboBox.getItems().addAll(basket.getCosegregation());
        comparabilityComboBox.getItems().addAll(basket.getComparability());

        //Create tooltips
        addTooltip(varPositionTextField, "Genomic position of variant in 1-based (VCF style) numbering");
        addTooltip(varReferenceTextField, "Representation of reference allele in VCF style (see help)");
        addTooltip(varAlternateTextField, "Representation of alternate allele in VCF style (see help)");
        addTooltip(crypticSpliceSitePositionTextField, "Genomic position of the nucleotide left (5' direction)" +
                "from novel exon/intron boundary\n E.g.: atcaG|cacatg <-- position of capital 'G' in ref genome.\n" +
                "(1-based numbering)");
        addTooltip(crypticSpliceSiteTypeComboBox, "What kind of CSS? 3' or 5' splice site?");
        addTooltip(crypticSpliceSiteSnippetTextField, "Nucleotide sequence of novel exon/intron boundary." +
                "Indicate the boundary using '|' symbol.\nWrite nucleotide sequence of FWD genomic strand regardless " +
                "of the gene strand or CSS type.\n" +
                "E.g: intron|exon, exon|intron");
    }


    /**
     * Create bindings of FXML view elements with model fields to ensure proper synchronization between model & view content
     */
    @Override
    protected void initializeBindings() {
        varChromosomeComboBox.valueProperty().bindBidirectional(variant.chromosomeProperty());
        varPositionTextField.textProperty().bindBidirectional(variant.positionProperty());
        varReferenceTextField.textProperty().bindBidirectional(variant.referenceAlleleProperty());
        varAlternateTextField.textProperty().bindBidirectional(variant.alternateAlleleProperty());
        varSnippetTextField.textProperty().bindBidirectional(variant.snippetProperty());
        varGenotypeComboBox.valueProperty().bindBidirectional(variant.genotypeProperty());
        varClassComboBox.valueProperty().bindBidirectional(variant.variantClassProperty());
        varPathomechanismComboBox.valueProperty().bindBidirectional(variant.pathomechanismProperty());
        varConsequenceComboBox.valueProperty().bindBidirectional(variant.consequenceProperty());
        crypticSpliceSitePositionTextField.textProperty().bindBidirectional(variant.crypticPositionProperty());
        crypticSpliceSiteTypeComboBox.valueProperty().bindBidirectional(variant.crypticSpliceSiteTypeProperty());
        crypticSpliceSiteSnippetTextField.textProperty().bindBidirectional(variant.crypticSpliceSiteSnippetProperty());

        // Validation checkboxes
        minigeneCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().minigeneValidationProperty());
        siteDirectedMutagenesisCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().siteDirectedMutagenesisValidationProperty());
        rtPCRCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().rtPCRValidationProperty());
        cDNASeqencingCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().cDNASequencingValidationProperty());
        pcrCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().pcrValidationProperty());
        srProteinOverexpressionCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().srProteinOverexpressionValidationProperty());
        srProteinKnockdownCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().srProteinKnockdownValidationProperty());
        mutationOfWTSpliceSiteCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().mutOfWTSpliceSiteValidationProperty());
        otherCheckBox.selectedProperty().bindBidirectional(variant.getSplicingValidation().otherValidationProperty());

        // Other validation stuff
        cosegregationComboBox.valueProperty().bindBidirectional(variant.cosegregationProperty());
        comparabilityComboBox.valueProperty().bindBidirectional(variant.comparabilityProperty());

        // Set title to pane
        this.setText(variant.getVariantMode().name());
    }
}
