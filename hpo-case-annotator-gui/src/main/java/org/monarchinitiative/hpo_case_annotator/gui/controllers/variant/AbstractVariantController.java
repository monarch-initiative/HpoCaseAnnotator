package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import javafx.application.HostServices;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.AbstractDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Place for shared content of VariantControllers. Needs to be subclassed by every class that wants to act as a controller
 * of {@link Variant} model class and be placed in {@link DiseaseCaseDataController}.
 * <p>
 * All the variants are validated by the same validator, hence the validator is defined in this class.
 * Created by ielis on 5/17/17.
 */
public abstract class AbstractVariantController extends AbstractDataController<Variant> {

    /**
     * POJO containing data to be used for populating content of FXML elements such as ComboBoxes.
     */
    final GuiElementValues elementValues;

    final List<ValidationResult> validationResults;

    private final ValidationRunner<Variant> variantValidationRunner;
    /**
     * Allows to open hyperlink in OS-dependent default web browser.
     */
    private final HostServices hostServices;


    AbstractVariantController(GuiElementValues elementValues,HostServices hostServices) {
        this.elementValues = elementValues;
        this.variantValidationRunner = ValidationRunner.variantValidationRunner();
        this.validationResults = new ArrayList<>();
        this.hostServices=hostServices;
    }


    @Override
    public boolean isComplete() {
        validationResults.clear();
        validationResults.addAll(variantValidationRunner.validateSingleModel(getData()));
        return validationResults.isEmpty();
    }

    public abstract Binding<String> variantTitleBinding();

    /**
     * Utility method for keeping track of {@link Observable}s that the {@link Variant} depends on.
     *
     * @return {@link List} with observable dependencies
     */
    abstract List<? extends Observable> getObservableVariantDependencies();

    protected void showVariantValidator() {
        String assembl=this.elementValues.getGenomeBuild().get(0).toString();
        String chrom =this.elementValues.getChromosome().get(0);

    }

    /**
     * This method can be used by any of the variant controllers. Its function is to open a
     * webpage on the VariantValidator website
     * @param assembly The Genome build (hg37 or hg38)
     * @param chrom The chromosome as a string (chr1 or 1)
     * @param pos the position of the variant on the chromosome
     * @param ref The reference base(s)
     * @param alt The alternative base(s)
     */
    protected void goToVariantValidatorWebsite(GenomeAssembly assembly, String chrom, int pos, String ref, String alt) {
        String genomeAssmblyString="GRCh37";
        if (assembly.equals(GenomeAssembly.GRCH_38)) {
            genomeAssmblyString="GRCh38";
        }
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


    /**
     * Currently, this app does not have information about the transcripts that are associated with a given
     * gene symbol.
     * TODO -- use an API to get all relevant accession numbers and offer them to the user as a combo box.
     */
    protected void getTranscriptDataAndGoToVariantValidatorWebsite() {
         Optional<List<String>> opt = PopUps.getPairOfUserStrings(null,
                "Transcript data for VariantValidator",
                "enter accession number and variant (e.g., NM_000088.3 and c.589G>T)",
                "accession","variant");
        if (!opt.isPresent()) {
            PopUps.showInfoMessage("Error","Could not extract HGVS data for VariantValidator");
            return;
        }

        List<String> results = opt.get();
        if (results.size()!=2) {
            PopUps.showInfoMessage("Malformed HGVS String","Could not parse HVGS String");
            return;
        }
        String transcript=results.get(0);
        String var=results.get(1);
        if (var.startsWith("c.")) {
            var=var.substring(2);
        } else {
            PopUps.showInfoMessage("Malformed HGVS String","Could not find \"c.\"");
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
            PopUps.showInfoMessage("Malformed HGVS String","Could not parse position");

        }
    }


}
