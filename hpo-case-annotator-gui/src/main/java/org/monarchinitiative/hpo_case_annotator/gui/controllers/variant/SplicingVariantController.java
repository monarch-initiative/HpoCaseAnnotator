package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#SPLICING} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class SplicingVariantController extends AbstractVariantController {

    private BooleanBinding isCompleteBinding;


    // ******************** FXML elements, injected by FXMLLoader ********************************** //
    // ************************* Variant *********************************** //
    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

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
    @Inject
    public SplicingVariantController(GuiElementValues elementValues) {
        super(elementValues);
    }


    /**
     * Initialize content of fxml view elements, create tooltips.
     */
    public void initialize() {
        //        this.setText(VariantValidation.Context.SPLICING.toString());
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        varChromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        varPositionTextField.setTextFormatter(makeTextFormatter(varPositionTextField, VARIANT_POSITION_REGEXP));
        varReferenceTextField.setTextFormatter(makeTextFormatter(varReferenceTextField, ALLELE_REGEXP));
        varAlternateTextField.setTextFormatter(makeTextFormatter(varAlternateTextField, ALLELE_REGEXP));
        varSnippetTextField.setTextFormatter(makeTextFormatter(varSnippetTextField, SNIPPET_REGEXP));
        varGenotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        varClassComboBox.getItems().addAll(elementValues.getVariantClass());
        varPathomechanismComboBox.getItems().addAll(elementValues.getPathomechanism());
        varConsequenceComboBox.getItems().addAll(elementValues.getConsequence());
        crypticSpliceSitePositionTextField.setTextFormatter(makeTextFormatter(crypticSpliceSitePositionTextField, VARIANT_POSITION_REGEXP));
        crypticSpliceSiteTypeComboBox.getItems().addAll(Arrays.stream(CrypticSpliceSiteType.values()).filter(c -> !c.equals(CrypticSpliceSiteType.UNRECOGNIZED)).collect(Collectors.toList()));
        crypticSpliceSiteSnippetTextField.setTextFormatter(makeTextFormatter(crypticSpliceSiteSnippetTextField, CSS_SNIPPET_REGEXP));
        cosegregationComboBox.getItems().addAll(elementValues.getCosegregation());
        comparabilityComboBox.getItems().addAll(elementValues.getComparability());

        //Create tooltips
        // Create tooltips here
        decorateWithTooltip(varPositionTextField, "Genomic position of variant in 1-based (VCF style) numbering");
        decorateWithTooltip(varReferenceTextField, "Representation of reference allele in VCF style (see help)");
        decorateWithTooltip(varAlternateTextField, "Representation of alternate allele in VCF style (see help)");
        decorateWithTooltip(varSnippetTextField, "Snippet of nucleotide sequence near variant, e.g. 'ACGT[A/C]ACTG'");
        decorateWithTooltip(crypticSpliceSitePositionTextField, "Genomic position of the nucleotide left (5' direction)" +
                "from novel exon/intron boundary\n E.g.: atcaG|cacatg <-- position of capital 'G' in ref genome.\n" +
                "(1-based numbering)");
        decorateWithTooltip(crypticSpliceSiteTypeComboBox, "What kind of CSS? 3' or 5' splice site?");
        decorateWithTooltip(crypticSpliceSiteSnippetTextField, "Nucleotide sequence of novel exon/intron boundary." +
                "Indicate the boundary using '|' symbol.\nWrite nucleotide sequence of FWD genomic strand regardless " +
                "of the gene strand or CSS type.\n" +
                "E.g: intron|exon, exon|intron");

        // value of the ComboBox is null if user did not click on anything, TextField contains empty string
        isCompleteBinding = Bindings.createBooleanBinding(() -> varChromosomeComboBox.getValue() != null &&
                        varPositionTextField.getText() != null && varPositionTextField.getText().matches(VARIANT_POSITION_REGEXP) &&
                        varReferenceTextField.getText() != null && varReferenceTextField.getText().matches(ALLELE_REGEXP) &&
                        varAlternateTextField.getText() != null && varAlternateTextField.getText().matches(ALLELE_REGEXP) &&
                        varSnippetTextField.getText() != null && varSnippetTextField.getText().matches(SNIPPET_REGEXP) &&
                        varGenotypeComboBox.getValue() != null,
                varChromosomeComboBox.valueProperty(), varPositionTextField.textProperty(), varReferenceTextField.textProperty(),
                varAlternateTextField.textProperty(), varSnippetTextField.textProperty(), varGenotypeComboBox.valueProperty());
    }

    @Override
    public void presentVariant(Variant variant) {
        // Variant related fields
        genomeBuildComboBox.setValue(variant.getVariantPosition().getGenomeAssembly());
        varChromosomeComboBox.setValue(variant.getVariantPosition().getContig());
        varPositionTextField.setText(String.valueOf(variant.getVariantPosition().getPos()));
        varReferenceTextField.setText(variant.getVariantPosition().getRefAllele());
        varAlternateTextField.setText(variant.getVariantPosition().getAltAllele());
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
        if (isCompleteBinding.get()) {
            return Variant.newBuilder()
                    .setVariantPosition(VariantPosition.newBuilder()
                            .setGenomeAssembly(genomeBuildComboBox.getValue())
                            .setContig(varChromosomeComboBox.getValue())
                            .setPos(Integer.parseInt(varPositionTextField.getText()))
                            .setRefAllele(varReferenceTextField.getText())
                            .setAltAllele(varAlternateTextField.getText())
                            .build())
                    .setSnippet(varSnippetTextField.getText())
                    .setGenotype(varGenotypeComboBox.getValue())
                    .setVariantClass(varClassComboBox.getValue() == null ? "" : varClassComboBox.getValue())
                    .setPathomechanism(varPathomechanismComboBox.getValue() == null ? "" : varPathomechanismComboBox.getValue())
                    .setConsequence(varConsequenceComboBox.getValue() == null ? "" : varConsequenceComboBox.getValue())
                    .setCrypticPosition(Integer.parseInt(crypticSpliceSitePositionTextField.getText().isEmpty() ? "0" : crypticSpliceSitePositionTextField.getText()))
                    .setCrypticSpliceSiteType(crypticSpliceSiteTypeComboBox.getValue() == null ? CrypticSpliceSiteType.NO : crypticSpliceSiteTypeComboBox.getValue())
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
                            .setCosegregation(cosegregationComboBox.getValue() != null && cosegregationComboBox.getValue().equals("yes"))
                            .setComparability(comparabilityComboBox.getValue() != null && comparabilityComboBox.getValue().equals("yes"))
                            .build())
                    .build();
        } else {
            return Variant.getDefaultInstance();
        }
    }

    @Override
    public BooleanBinding isCompleteBinding() {
        return isCompleteBinding;
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isCompleteBinding.get()) {
                        return String.format("Splicing variant: %s_%s:%s%s>%s", genomeBuildComboBox.getValue(), varChromosomeComboBox.getValue(),
                                varPositionTextField.getText(), varReferenceTextField.getText(),
                                varAlternateTextField.getText());
                    } else {
                        return "Splicing variant: INCOMPLETE";
                    }
                },
                genomeBuildComboBox.valueProperty(), varChromosomeComboBox.valueProperty(), varPositionTextField.textProperty(),
                varReferenceTextField.textProperty(), varAlternateTextField.textProperty(), varSnippetTextField.textProperty(),
                varGenotypeComboBox.valueProperty());
    }
}
