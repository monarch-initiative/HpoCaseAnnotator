package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import htsjdk.samtools.util.Interval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverAdapter;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import javax.inject.Inject;
import java.util.Optional;

public class LiftoverController {
    private final GuiElementValues guiElementValues;
    private final LiftOverAdapter liftOverAdapter;
    public ComboBox<GenomeAssembly> sourceAssembly;
    public ComboBox<String> sourceContig;
    public TextField sourceStart;
    public ComboBox<GenomeAssembly> targetAssembly;
    public TextField output;

    @Inject
    public LiftoverController(GuiElementValues guiElementValues, LiftOverAdapter liftOverAdapter) {
        this.guiElementValues = guiElementValues;
        this.liftOverAdapter = liftOverAdapter;
    }

    public void initialize() {
        sourceAssembly.getItems().addAll(liftOverAdapter.supportedConversions().keySet());
        sourceAssembly.valueProperty().addListener((obs, o, n) -> targetAssembly.getItems().addAll(liftOverAdapter.supportedConversions().get(n)));

        sourceContig.getItems().addAll(guiElementValues.getChromosome());
        output.textProperty().bind(setupLiftoverCalculation());
    }

    private StringBinding setupLiftoverCalculation() {
        return Bindings.createStringBinding(() -> {
                    int pos;
                    try {
                        pos = Integer.parseInt(sourceStart.getText());
                    } catch (NumberFormatException e) {
                        return "Unparsable position";
                    }
                    String contig = sourceContig.getValue().startsWith("chr") ? sourceContig.getValue() : "chr" + sourceContig.getValue();
                    Interval interval = new Interval(contig, pos, pos);
                    Optional<Interval> lifted = liftOverAdapter.liftOver(interval, sourceAssembly.getValue(), targetAssembly.getValue());

                    return lifted.map(i -> String.format("%s:%d", i.getContig(), i.getStart()))
                            .orElse("Unable to lift " + contig + ":" + sourceStart.getText());
                },
                sourceAssembly.valueProperty(), sourceContig.valueProperty(), sourceStart.textProperty());
    }
}
