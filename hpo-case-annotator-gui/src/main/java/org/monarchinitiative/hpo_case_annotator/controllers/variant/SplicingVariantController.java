package org.monarchinitiative.hpo_case_annotator.controllers.variant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.model.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.model.proto.CrypticSpliceSiteType;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#SPLICING} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SplicingVariantController extends AbstractVariantController {

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
    private ComboBox<Genotype> varGenotypeComboBox;

    @FXML
    private ComboBox<String> varClassComboBox;

    @FXML
    private ComboBox<String> varPathomechanismComboBox;

    @FXML
    private ComboBox<String> varConsequenceComboBox;

    @FXML
    private TextField crypticSpliceSitePositionTextField;

    @FXML
    private ComboBox<CrypticSpliceSiteType> crypticSpliceSiteTypeComboBox;

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
     */
    public SplicingVariantController(ChoiceBasket choiceBasket) throws IOException {
        super(choiceBasket);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SplicingVariantView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        super.getStylesheets().add(getClass().getResource("VariantView.css").toExternalForm());
        loader.load();

        populateContent();
    }


    /**
     * Read yaml configuration file and initialize content of fxml view elements. Create tooltips.
     */
    @Override
    protected void populateContent() {
        this.setText(VariantValidation.Context.SPLICING.toString());

        varChromosomeComboBox.getItems().addAll(choiceBasket.getChromosome());
        varGenotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        varClassComboBox.getItems().addAll(choiceBasket.getVariantClass());
        varPathomechanismComboBox.getItems().addAll(choiceBasket.getPathomechanism());
        varConsequenceComboBox.getItems().addAll(choiceBasket.getConsequence());
        crypticSpliceSiteTypeComboBox.getItems().addAll(Arrays.stream(CrypticSpliceSiteType.values()).filter(c -> !c.equals(CrypticSpliceSiteType.UNRECOGNIZED)).collect(Collectors.toList()));
        cosegregationComboBox.getItems().addAll(choiceBasket.getCosegregation());
        comparabilityComboBox.getItems().addAll(choiceBasket.getComparability());

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


    @Override
    public void presentVariant(Variant variant) {
        // Variant related fields
        varChromosomeComboBox.setValue(variant.getContig());
        varPositionTextField.setText(String.valueOf(variant.getPos()));
        varReferenceTextField.setText(variant.getRefAllele());
        varAlternateTextField.setText(variant.getAltAllele());
        varSnippetTextField.setText(variant.getSnippet());
        varGenotypeComboBox.setValue(variant.getGenotype());
        varClassComboBox.setValue(variant.getVariantClass());
        varPathomechanismComboBox.setValue(variant.getPathomechanism());
        varConsequenceComboBox.setValue(variant.getConsequence());

        crypticSpliceSitePositionTextField.setText(String.valueOf(variant.getCrypticPosition()));
        crypticSpliceSiteTypeComboBox.setValue(variant.getCrypticSpliceSiteType());
        crypticSpliceSiteSnippetTextField.setText(variant.getCrypticSpliceSiteSnippet());

        // Validation checkboxes
        VariantValidation validation = variant.getVariantValidation();
        minigeneCheckBox.setSelected(validation.getMinigeneValidation());
        siteDirectedMutagenesisCheckBox.setSelected(validation.getSiteDirectedMutagenesisValidation());
        rtPCRCheckBox.setSelected(validation.getRtPcrValidation());
        cDNASeqencingCheckBox.setSelected(validation.getCDnaSequencingValidation());
        pcrCheckBox.setSelected(validation.getPcrValidation());
        srProteinOverexpressionCheckBox.setSelected(validation.getSrProteinOverexpressionValidation());
        srProteinKnockdownCheckBox.setSelected(validation.getSrProteinKnockdownValidation());
        mutationOfWTSpliceSiteCheckBox.setSelected(validation.getMutOfWtSpliceSiteValidation());
        otherCheckBox.setSelected(validation.getOtherValidation());

        // Others
        cosegregationComboBox.setValue(validation.getCosegregation() ? "yes" : "no");
        comparabilityComboBox.setValue(validation.getComparability() ? "yes" : "no");
    }


    @Override
    public Variant getVariant() {
        return Variant.newBuilder()
                .setContig(varChromosomeComboBox.getValue())
                .setPos(Integer.parseInt(varPositionTextField.getText())) // TODO - make sure that the int is an int here
                .setRefAllele(varReferenceTextField.getText())
                .setAltAllele(varAlternateTextField.getText())
                .setSnippet(varSnippetTextField.getText())
                .setGenotype(varGenotypeComboBox.getValue())
                .setVariantClass(varClassComboBox.getValue())
                .setPathomechanism(varPathomechanismComboBox.getValue())
                .setConsequence(varConsequenceComboBox.getValue())
                .setCrypticPosition(Integer.parseInt(crypticSpliceSitePositionTextField.getText()))
                .setCrypticSpliceSiteType(crypticSpliceSiteTypeComboBox.getValue())
                .setCrypticSpliceSiteSnippet(crypticSpliceSitePositionTextField.getText())
                .setVariantValidation(VariantValidation.newBuilder()
                        .setContext(VariantValidation.Context.SPLICING)
                        .setMinigeneValidation(minigeneCheckBox.isSelected())
                        .setSiteDirectedMutagenesisValidation(siteDirectedMutagenesisCheckBox.isSelected())
                        .setRtPcrValidation(rtPCRCheckBox.isSelected())
                        .setCDnaSequencingValidation(cDNASeqencingCheckBox.isSelected())
                        .setPcrValidation(pcrCheckBox.isSelected())
                        .setSrProteinOverexpressionValidation(srProteinOverexpressionCheckBox.isSelected())
                        .setSrProteinKnockdownValidation(srProteinKnockdownCheckBox.isSelected())
                        .setMutOfWtSpliceSiteValidation(mutationOfWTSpliceSiteCheckBox.isSelected())
                        .setOtherValidation(otherCheckBox.isSelected())

                        .setCosegregation(cosegregationComboBox.getValue().equals("yes"))
                        .setComparability(comparabilityComboBox.getValue().equals("yes"))
                        .build())
                .build();
    }
}
