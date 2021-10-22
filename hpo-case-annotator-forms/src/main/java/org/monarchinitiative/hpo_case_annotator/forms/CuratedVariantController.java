package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.variant.HgvsVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.VcfBreakendVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.VcfSequenceVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.VcfSymbolicVariantController;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

public class CuratedVariantController implements ComponentController<CuratedVariant> {

    public TabPane variantsTabPane;
    public VBox sequenceVariant;
    public VcfSequenceVariantController sequenceVariantController;
    public VBox symbolicVariant;
    public VcfSymbolicVariantController symbolicVariantController;
    public VBox breakendVariant;
    public VcfBreakendVariantController breakendVariantController;
    public VBox hgvsVariant;
    public HgvsVariantController hgvsVariantController;

    public HBox statusBar;
    public StatusBarController statusBarController;


    @Override
    public void presentComponent(CuratedVariant variant) {
        // TODO: 10/22/21 present either sequence or symbolic variant
    }

    @Override
    public CuratedVariant getComponent() {
        String id = variantsTabPane.getSelectionModel().getSelectedItem().getId();
        return switch (id) {
            case "vcfSequenceVariantTab" -> processSequenceVariant();
            case "vcfSymbolicVariantTab" -> processSymbolicVariant();
            case "vcfBreakendVariantTab" -> processBreakendVariant();
            case "hgvsVariantTab" -> processHgvsVariant();
            default -> throw new RuntimeException("Bug - unknown variant tab `" + id + '`');
        };
    }

    private CuratedVariant processSequenceVariant() {
        // TODO: 10/22/21 implement
        return null;
    }

    private CuratedVariant processSymbolicVariant() {
        // TODO: 10/22/21 implement
        return null;
    }

    private CuratedVariant processBreakendVariant() {
        // TODO: 10/22/21 implement
        return null;
    }

    private CuratedVariant processHgvsVariant() {
        // TODO: 10/22/21 implement
        return null;
    }
}
