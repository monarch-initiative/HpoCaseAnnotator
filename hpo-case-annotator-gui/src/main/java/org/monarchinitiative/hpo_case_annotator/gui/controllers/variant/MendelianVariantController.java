package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted.SequenceDao;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
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

    // we fetch +-10bp bases for a variant during snippet generation
    private static final int GENERATED_SNIPPET_PADDING = 10;

    // ******************** FXML elements, injected by FXMLLoader ********************************** //
    @FXML
    public ComboBox<GenomeAssembly> genomeBuildComboBox;

    @FXML
    public Button getAccessionsButton;

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
    private final GenomeAssemblies genomeAssemblies;

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
    private final ExecutorService executorService;
    @FXML
    public Button generateSnippetButton;

    /**
     * Create instance of this class which acts as a controller from MVC pattern.
     */
    @Inject
    public MendelianVariantController(GuiElementValues elementValues,
                                      HostServicesWrapper hostServices,
                                      GenomeAssemblies genomeAssemblies,
                                      ExecutorService executorService) {
        super(hostServices, elementValues);
        this.genomeAssemblies = genomeAssemblies;
        this.executorService = executorService;
    }


    @FXML
    private void initialize() {
        genomeBuildComboBox.getItems().addAll(elementValues.getGenomeBuild());
        chromosomeComboBox.getItems().addAll(elementValues.getChromosome());
        // add text formatter to impose constraint on the field
        positionTextField.setTextFormatter(makeTextFormatter(positionTextField, POSITIVE_INTEGER_REGEXP));
        referenceTextField.setTextFormatter(makeTextFormatter(referenceTextField, NONEMPTY_ALLELE_REGEXP));
        alternateTextField.setTextFormatter(makeTextFormatter(alternateTextField, NONEMPTY_ALLELE_REGEXP));
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
        decorateWithTooltipOnFocus(generateSnippetButton, "Generate snippet if the FASTA file is present");

        // enable
        BooleanBinding allDataForSnippetGenerationIsPresent = Bindings.createBooleanBinding(
                () -> genomeAssemblies.hasFastaForAssembly(genomeBuildComboBox.getValue())
                        && chromosomeComboBox.getValue() != null
                        && POSITIVE_INTEGER_REGEXP.matcher(positionTextField.getText()).matches()
                        && NONEMPTY_ALLELE_REGEXP.matcher(referenceTextField.getText()).matches()
                        && NONEMPTY_ALLELE_REGEXP.matcher(alternateTextField.getText()).matches(),
                genomeBuildComboBox.valueProperty(), chromosomeComboBox.valueProperty(), positionTextField.textProperty(),
                referenceTextField.textProperty(), alternateTextField.textProperty());
        generateSnippetButton.disableProperty().bind(allDataForSnippetGenerationIsPresent.not());
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
        int pos = parseIntOrGetDefaultValue(positionTextField::getText, -1);

        return Variant.newBuilder()
                .setVariantPosition(VariantPosition.newBuilder()
                        .setGenomeAssembly(genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue())
                        .setContig(chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue())
                        .setPos(pos)
                        .setRefAllele(referenceTextField.getText())
                        .setAltAllele(alternateTextField.getText())
                        .build())
                .setSnippet(snippetTextField.getText())
                .setGenotype(genotypeComboBox.getValue() == null ? Genotype.UNDEFINED : genotypeComboBox.getValue())
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
                        String ref = referenceTextField.getText().length() > 10 ? referenceTextField.getText().substring(0, 10) + "..." : referenceTextField.getText();
                        String alt = alternateTextField.getText().length() > 10 ? alternateTextField.getText().substring(0, 10) + "..." : alternateTextField.getText();
                        return String.format("Mendelian variant: %s %s:%s%s>%s",
                                genomeBuildComboBox.getValue(), chromosomeComboBox.getValue(), positionTextField.getText(),
                                ref, alt);
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

    @FXML
    private void variantValidatorToClipboardTranscript() {
        GenomeAssembly assembly = genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue();
        VariantUtil.getTranscriptDataAndGoToVariantValidatorWebsite(assembly, hostServices);
    }

    /**
     * Open up a page on the VariantValidator website that allows the curator to check whether the genomic coordinates
     * match the entered mutation data. It uses the method
     * {@link VariantUtil#goToVariantValidatorWebsite(GenomeAssembly, String, int, String, String, HostServicesWrapper)}
     * to display the variant on the VariantValidator website.
     */
    @FXML
    private void showVariantValidator() {
        GenomeAssembly assembly = genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue();
        String chrom = chromosomeComboBox.getValue() == null ? "NA" : chromosomeComboBox.getValue();
        Variant variant = getData();
        int pos = variant.getVariantPosition().getPos();
        String ref = this.referenceTextField.getText();
        String alt = this.alternateTextField.getText();
        VariantUtil.goToVariantValidatorWebsite(assembly, chrom, pos, ref, alt, this.hostServices);
    }


    @FXML
    public void getAccessionsButtonAction() {
        String entrezId = getEntrezId(); // This is the gene ID entered by the user.
        GenomeAssembly assembly = genomeBuildComboBox.getValue() == null ? GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY : genomeBuildComboBox.getValue();
        Window window = getAccessionsButton.getScene().getWindow();
        VariantUtil.geneAccessionNumberGrabber(entrezId, assembly, hostServices, window);
    }

    @FXML
    public void generateSnippetAction() {
        Runnable generateSnippet = () -> {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Generating snippet for `{}:{}:{}{}>{}`",
                        genomeBuildComboBox.getValue(), chromosomeComboBox.getValue(), positionTextField.getText(), referenceTextField.getText(), alternateTextField.getText());

            Optional<SequenceDao> sdOpt = genomeAssemblies.getSequenceDaoForAssembly(genomeBuildComboBox.getValue());
            if (!sdOpt.isPresent()) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Genome assembly `{}` is missing even though it should be present", genomeBuildComboBox.getValue());
                return;
            }
            SequenceDao sequenceDao = sdOpt.get();
            int variantPos = Integer.parseInt(positionTextField.getText());

            int upstreamStart = variantPos - GENERATED_SNIPPET_PADDING - 1;
            String upstreamSeq = sequenceDao.fetchSequence(chromosomeComboBox.getValue(), upstreamStart, upstreamStart + GENERATED_SNIPPET_PADDING);
            int downstreamStart = variantPos + referenceTextField.getText().length() - 1;
            String downstreamSeq = sequenceDao.fetchSequence(chromosomeComboBox.getValue(), downstreamStart, downstreamStart + GENERATED_SNIPPET_PADDING);

            String snippet = upstreamSeq + '[' + referenceTextField.getText() + '/' + alternateTextField.getText() + ']' + downstreamSeq;
            Platform.runLater(() -> snippetTextField.setText(snippet));
        };
        executorService.submit(generateSnippet);
    }
}
