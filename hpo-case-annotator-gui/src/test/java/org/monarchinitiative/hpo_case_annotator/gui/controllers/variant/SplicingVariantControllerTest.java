package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class SplicingVariantControllerTest extends ApplicationTest {

    @Inject
    private Injector injector;

    private SplicingVariantController controller;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        // for headless GUI testing, set the "not.headless" system property to true or comment out if you want to see the
        // robot in action
        if (!Boolean.getBoolean("not.headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1200x760-32");
        }
    }

    /**
     * The minimal information for variant to be valid is
     * <ul>
     * <li>chromosome</li>
     * <li>position</li>
     * <li>reference</li>
     * <li>alternate</li>
     * <li>snippet</li>
     * <li>genotype</li>
     * </ul>
     */
    @Test
    public void variantIsCompleteBinding() {
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#genomeBuildComboBox")
                .moveBy(-10, 30)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varChromosomeComboBox")
                .moveBy(0, 30)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varPositionTextField")
                .write("12");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varReferenceTextField")
                .write("C");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varAlternateTextField")
                .write("A");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varSnippetTextField")
                .write("CT[C/A]AA");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#varGenotypeComboBox")
                .moveBy(0, 60)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isCompleteBinding().get(), is(true));

        // assert
        Variant variant = controller.getVariant();
        final VariantPosition vp = variant.getVariantPosition();
        assertThat(vp.getGenomeAssembly(), is(GenomeAssembly.GRCH_37));
        assertThat(vp.getContig(), is("1"));
        assertThat(vp.getPos(), is(12));
        assertThat(vp.getRefAllele(), is("C"));
        assertThat(vp.getAltAllele(), is("A"));
        assertThat(variant.getSnippet(), is("CT[C/A]AA"));
        assertThat(variant.getGenotype(), is(Genotype.HETEROZYGOUS));
    }

    @Test
    public void getIncompleteVariant() {
        // we did not set any data, hence the variant is incomplete
        assertThat(controller.getVariant(), is(equalTo(Variant.getDefaultInstance())));
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MendelianVariantController.class.getResource("SplicingVariant.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }


}