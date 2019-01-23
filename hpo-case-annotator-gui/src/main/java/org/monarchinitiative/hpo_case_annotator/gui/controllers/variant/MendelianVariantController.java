package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.application.HostServices;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.monarchinitiative.hpo_case_annotator.core.validation.VariantSyntaxValidator.*;

/**
 * Controller class responsible for presenting {@link org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation.Context#MENDELIAN} variants.
 * Acts as a controller in the MVC pattern.
 * <p>
 * Created by Daniel Danis.
 */
public final class MendelianVariantController extends AbstractVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MendelianVariantController.class);

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
     * Create instance of this class which acts as a controller from MVC pattern.
     */
    @Inject
    public MendelianVariantController(GuiElementValues elementValues,HostServices hostServices) {
        super(elementValues,hostServices);
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
        cosegregationComboBox.getItems().addAll(elementValues.getCosegregation());
        comparabilityComboBox.getItems().addAll(elementValues.getComparability());
        otherChoicesComboBox.getItems().addAll(elementValues.getOtherChoices());
        otherEffectComboBox.getItems().addAll(elementValues.getOtherEffect());

        // Create tooltips here
        decorateWithTooltipOnFocus(positionTextField, "Genomic position of the first nucleotide of REF allele \n(positive integer, 1-based numbering)");
        decorateWithTooltipOnFocus(referenceTextField, "Representation of reference allele in VCF style");
        decorateWithTooltipOnFocus(alternateTextField, "Representation of alternate allele in VCF style");
        decorateWithTooltipOnFocus(snippetTextField, "Snippet of nucleotide sequence near variant, e.g. 'ACGT[A/C]ACTG'");
    }

    @Override
    public void presentData(Variant variant) {
        if (variant.getVariantValidation().getContext().equals(VariantValidation.Context.MENDELIAN)) {
            // Variant
            genomeBuildComboBox.setValue(variant.getVariantPosition().getGenomeAssembly());
            chromosomeComboBox.setValue(variant.getVariantPosition().getContig());
            // do not set zero, but rather an empty string
            positionTextField.setText(variant.getVariantPosition().getPos() == 0 ? "" : String.valueOf(variant.getVariantPosition().getPos()));
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
    public Variant getData() {
        int pos;
        try {
            pos = Integer.parseInt(positionTextField.getText());
        } catch (NumberFormatException nfe) {
            pos = 0;
        }

        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY: genomeBuildComboBox.getValue())
                        .setContig(chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue())
                        .setPos(pos)
                        .setRefAllele(referenceTextField.getText())
                        .setAltAllele(alternateTextField.getText())
                        .build())
                .setSnippet(snippetTextField.getText())
                .setGenotype(genotypeComboBox.getValue() == null ? Genotype.UNDEFINED: genotypeComboBox.getValue())
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
    }

    @Override
    public Binding<String> variantTitleBinding() {
        return Bindings.createStringBinding(() -> {
                    if (isComplete()) {
                        return String.format("Mendelian variant: %s %s:%s%s>%s",
                                genomeBuildComboBox.getValue(), chromosomeComboBox.getValue(), positionTextField.getText(),
                                referenceTextField.getText(), alternateTextField.getText());
                    } else {
                        return "Mendelian variant: INCOMPLETE: " + validationResults.get(0).getMessage();
                    }
                },
                getObservableVariantDependencies().toArray(new Observable[0]));
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

    @FXML public void variantValidatorToClipboardTranscript() {
        String tra=PopUps.getStringFromUser("Enter HGVS-encoded variant for VariantValidator",
                "HGVS", "Enter variant (e.g., NM_000088.3:c.589G>T):");
        String a[]=tra.split(":");
        if (a.length!=2) {
            PopUps.showWarningDialog("Malformed HGVS String","Could not parse HVGS String",tra);
            return;
        }
        String transcript=a[0];
        String var=a[1];
        if (var.startsWith("c.")) {
            var=var.substring(2);
        } else {
            PopUps.showWarningDialog("Malformed HGVS String","Could not find \"c.\"",tra);
            return;
        }
        Pattern pat = Pattern.compile("(\\d+)(\\w+)\\>(\\w+)");
        Matcher m = pat.matcher(var);
        if (m.matches()) {
            String pos = m.group(1);
            String ref=m.group(2);
            String alt=m.group(3);
            String vvURL =String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s:%s:%s",
                    transcript,
                    pos,
                    ref,
                    alt);
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(vvURL);
            clipboard.setContent(content);
        } else {
            PopUps.showWarningDialog("Malformed HGVS String","Could not parse position",tra);

        }
    }


    /**
     * Open up a page on the VariantValidator website that allows the curator to check whether the genomic coordinates
     * match the entered mutation data.
     */
    @FXML public void showVariantValidator() {
        String assembl=this.elementValues.getGenomeBuild().get(0).toString();
        String chrom =this.elementValues.getChromosome().get(0);
        int pos = this.getData().getVariantPosition().getPos();
        String ref = this.referenceTextField.getText();
        String alt = this.alternateTextField.getText();

        // Hard-code this for now
        // need to figure out how to code this for VariantValidator
        String genomeAssmblyString="GRCh37";
        if (chrom.startsWith("chr")) {
            chrom=chrom.substring(3);
        }
        // Create a URI for VariantValidator -- it will look like the following.
        //https://variantvalidator.org/variantvalidation/?variant=GRCh37:1:150550916:G:A
        String vvURL =String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s:%d:%s:%s",
                genomeAssmblyString,
                chrom,
                pos,
                ref,
                alt);
        // The following opens the system browser to the corresponding VariantValidator page
        Hyperlink hyper = new Hyperlink(vvURL);
        hostServices.showDocument(hyper.getText());

    }
}
