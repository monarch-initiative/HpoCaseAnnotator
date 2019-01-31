package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.application.HostServices;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.*;

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
     * Create instance of this class which acts as a controller from MVC pattern.
     */
    @Inject
    public SplicingVariantController(GuiElementValues elementValues, HostServices hostServices) {
        super(elementValues,hostServices);
    }


    /**
     * Initialize content of fxml view elements, create tooltips.
     */
    public void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        varChromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        // add text formatter to impose constraint on the field
        varPositionTextField.setTextFormatter(makeTextFormatter(varPositionTextField, POSITIVE_INTEGER_REGEXP));
        varReferenceTextField.setTextFormatter(makeTextFormatter(varReferenceTextField, ALLELE_REGEXP));
        varAlternateTextField.setTextFormatter(makeTextFormatter(varAlternateTextField, ALLELE_REGEXP));
        varSnippetTextField.setTextFormatter(makeTextFormatter(varSnippetTextField, SNIPPET_REGEXP));
        varGenotypeComboBox.getItems().addAll(Arrays.stream(Genotype.values()).filter(g -> !g.equals(Genotype.UNRECOGNIZED)).collect(Collectors.toList()));
        varClassComboBox.getItems().addAll(elementValues.getVariantClass());
        varPathomechanismComboBox.getItems().addAll(elementValues.getPathomechanism());
        varConsequenceComboBox.getItems().addAll(elementValues.getConsequence());
        crypticSpliceSitePositionTextField.setTextFormatter(makeToleratingTextFormatter(crypticSpliceSitePositionTextField, POSITIVE_INTEGER_REGEXP));
        crypticSpliceSiteTypeComboBox.getItems().addAll(Arrays.stream(CrypticSpliceSiteType.values()).filter(c -> !c.equals(CrypticSpliceSiteType.UNRECOGNIZED)).collect(Collectors.toList()));
        crypticSpliceSiteSnippetTextField.setTextFormatter(makeToleratingTextFormatter(crypticSpliceSiteSnippetTextField, CSS_SNIPPET_REGEXP));
        cosegregationComboBox.getItems().addAll(elementValues.getCosegregation());
        comparabilityComboBox.getItems().addAll(elementValues.getComparability());

        //Create tooltips
        // Create tooltips here
        decorateWithTooltipOnFocus(varPositionTextField, "Genomic position of the first nucleotide of REF allele \n(positive integer, 1-based numbering)");
        decorateWithTooltipOnFocus(varReferenceTextField, "Representation of reference allele in VCF style");
        decorateWithTooltipOnFocus(varAlternateTextField, "Representation of alternate allele in VCF style");
        decorateWithTooltipOnFocus(varSnippetTextField, "Snippet of nucleotide sequence near variant, e.g. 'ACGT[A/C]ACTG'");
        decorateWithTooltipOnFocus(crypticSpliceSitePositionTextField, "Genomic position of the nucleotide left (5' direction)" +
                "from novel exon/intron boundary\n E.g.: atcaG|cacatg <-- position of capital 'G' in ref genome.\n" +
                "(1-based numbering)");
        decorateWithTooltipOnFocus(crypticSpliceSiteTypeComboBox, "What kind of CSS? 3' or 5' splice site?");
        decorateWithTooltipOnFocus(crypticSpliceSiteSnippetTextField, "Nucleotide sequence of novel exon/intron boundary." +
                "Indicate the boundary using '|' symbol.\nWrite nucleotide sequence of FWD genomic strand regardless " +
                "of the gene strand or CSS type.\n" +
                "E.g: intron|exon, exon|intron");
    }

    @Override
    public void presentData(Variant variant) {
        // Variant related fields
        genomeBuildComboBox.setValue(variant.getVariantPosition().getGenomeAssembly());
        varChromosomeComboBox.setValue(variant.getVariantPosition().getContig());
        // do not set zero, but rather an empty string
        varPositionTextField.setText(variant.getVariantPosition().getPos() == 0 ? "" : String.valueOf(variant.getVariantPosition().getPos()));
        varReferenceTextField.setText(variant.getVariantPosition().getRefAllele());
        varAlternateTextField.setText(variant.getVariantPosition().getAltAllele());
        varSnippetTextField.setText(variant.getSnippet());
        varGenotypeComboBox.setValue(variant.getGenotype());
        varClassComboBox.setValue(variant.getVariantClass());
        varPathomechanismComboBox.setValue(variant.getPathomechanism());
        varConsequenceComboBox.setValue(variant.getConsequence());

        // do not set zero, but rather an empty string
        crypticSpliceSitePositionTextField.setText(variant.getCrypticPosition() == 0 ? "" : String.valueOf(variant.getCrypticPosition()));
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
    public Variant getData() {
        int pos;
        try {
            pos = Integer.parseInt(varPositionTextField.getText());
        } catch (NumberFormatException nfe) {
            pos = 0;
        }

        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue())
                        .setContig(varChromosomeComboBox.getValue() == null ? "NA" : varChromosomeComboBox.getValue())
                        .setPos(pos)
                        .setRefAllele(varReferenceTextField.getText())
                        .setAltAllele(varAlternateTextField.getText())
                        .build())
                .setSnippet(varSnippetTextField.getText())
                .setGenotype(varGenotypeComboBox.getValue() == null ? Genotype.UNDEFINED: varGenotypeComboBox.getValue())
                .setVariantClass(varClassComboBox.getValue() == null ? "" : varClassComboBox.getValue())
                .setPathomechanism(varPathomechanismComboBox.getValue() == null ? "" : varPathomechanismComboBox.getValue())
                .setConsequence(varConsequenceComboBox.getValue() == null ? "" : varConsequenceComboBox.getValue())
                .setCrypticPosition(Integer.parseInt(crypticSpliceSitePositionTextField.getText().isEmpty() ? "0" : crypticSpliceSitePositionTextField.getText()))
                .setCrypticSpliceSiteType(crypticSpliceSiteTypeComboBox.getValue() == null ? CrypticSpliceSiteType.NO_CSS : crypticSpliceSiteTypeComboBox.getValue())
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
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Splicing variant: %s %s:%s%s>%s", genomeBuildComboBox.getValue(), varChromosomeComboBox.getValue(),
                                varPositionTextField.getText(), varReferenceTextField.getText(),
                                varAlternateTextField.getText());
                    } else {
                        return "Splicing variant: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<? extends Observable> getObservableVariantDependencies() {
        return Arrays.asList(genomeBuildComboBox.valueProperty(), varChromosomeComboBox.valueProperty(), varPositionTextField.textProperty(),
                varReferenceTextField.textProperty(), varAlternateTextField.textProperty(), varSnippetTextField.textProperty(),
                varGenotypeComboBox.valueProperty());
    }
}
