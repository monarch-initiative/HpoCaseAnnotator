package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.gui.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantValidation;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class MendelianVariantControllerTest extends ApplicationTest {


    @Inject
    private Injector injector;

    private MendelianVariantController controller;

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


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MendelianVariantController.class.getResource("MendelianVariant.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
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

        clickOn("#chromosomeComboBox")
                .moveBy(0, 30)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#positionTextField")
                .write("12345345");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#referenceTextField")
                .write("C");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#alternateTextField")
                .write("A");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#snippetTextField")
                .write("CACT[C/A]AACCG");
        assertThat(controller.isCompleteBinding().get(), is(false));

        clickOn("#genotypeComboBox")
                .moveBy(0, 60)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isCompleteBinding().get(), is(true));

        // assert
        Variant variant = controller.getVariant();
        assertThat(variant.getContig(), is("1"));
        assertThat(variant.getPos(), is(12345345));
        assertThat(variant.getRefAllele(), is("C"));
        assertThat(variant.getAltAllele(), is("A"));
        assertThat(variant.getSnippet(), is("CACT[C/A]AACCG"));
        assertThat(variant.getGenotype(), is(Genotype.HETEROZYGOUS));
    }

    @Test
    public void getIncompleteVariant() {
        // we did not set any data, hence the variant is incomplete
        assertThat(controller.getVariant(), is(equalTo(Variant.getDefaultInstance())));
    }

    @Test
    public void getFullVariant() {
        // arange
        clickOn("#chromosomeComboBox").moveBy(0, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#positionTextField").write("12345345")
                .clickOn("#referenceTextField").write("C")
                .clickOn("#alternateTextField").write("A")
                .clickOn("#snippetTextField").write("CACT[C/A]AACCG")
                .clickOn("#genotypeComboBox").moveBy(0, 60).clickOn(MouseButton.PRIMARY)
                .clickOn("#variantClassComboBox").moveBy(0, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#pathomechanismComboBox").moveBy(0, 30).clickOn(MouseButton.PRIMARY)
                .clickOn("#regulatorTextField").write("regulator")
                .clickOn("#reporterComboBox").moveBy(0, 20).clickOn(MouseButton.PRIMARY)
                .clickOn("#residualActivityTextField").write("55")
                .clickOn("#emsaComboBox").moveBy(0, 25).clickOn(MouseButton.PRIMARY)
                .clickOn("#emsaTFSymbolTextField").write("GTA")
                .clickOn("#emsaGeneIDTextField").write("GENE")
                .clickOn("#cosegregationComboBox").moveBy(0, 25).clickOn(MouseButton.PRIMARY)
                .clickOn("#comparabilityComboBox").moveBy(0, 25).clickOn(MouseButton.PRIMARY)
                .clickOn("#otherChoicesComboBox").moveBy(0, 25).clickOn(MouseButton.PRIMARY)
                .clickOn("#otherEffectComboBox").moveBy(0, 25).clickOn(MouseButton.PRIMARY);
        // act
        Variant variant = controller.getVariant();
        // assert
        assertThat(variant.getContig(), is("1"));
        assertThat(variant.getPos(), is(12345345));
        assertThat(variant.getRefAllele(), is("C"));
        assertThat(variant.getAltAllele(), is("A"));
        assertThat(variant.getSnippet(), is("CACT[C/A]AACCG"));
        assertThat(variant.getGenotype(), is(Genotype.HETEROZYGOUS));
        assertThat(variant.getVariantClass(), is("coding"));
        assertThat(variant.getPathomechanism(), is("unknown"));

        VariantValidation validation = variant.getVariantValidation();
        assertThat(validation.getContext(), is(VariantValidation.Context.MENDELIAN));
        assertThat(validation.getCosegregation(), is(true));
        assertThat(validation.getComparability(), is(true));
        assertThat(validation.getRegulator(), is("regulator"));
        assertThat(validation.getReporterRegulation(), is("no"));
        assertThat(validation.getReporterResidualActivity(), is("55"));
        assertThat(validation.getEmsaValidationPerformed(), is(true));
        assertThat(validation.getEmsaTfSymbol(), is("GTA"));
        assertThat(validation.getEmsaGeneId(), is("GENE"));
        assertThat(validation.getOtherChoices(), is("no"));
        assertThat(validation.getOtherEffect(), is("Telomerase"));
    }

    @Test
    public void presentVariant() {
        final Variant variant = DiseaseCaseModelExample.benMahmoud2013B3GLCT().getVariant(0);
        final Variant hacked = variant.toBuilder()
                .setVariantValidation(variant.getVariantValidation().toBuilder()
                        .setContext(VariantValidation.Context.MENDELIAN)
                        .build())
                .build();
        Platform.runLater(() -> controller.presentVariant(hacked));
        sleep(30);

        final Variant received = controller.getVariant();
        assertThat(received.getContig(), is("13"));
        assertThat(received.getPos(), is(31843349));
        assertThat(received.getRefAllele(), is("A"));
        assertThat(received.getAltAllele(), is("G"));
        assertThat(received.getSnippet(), is("TTTCT[A/G]GGCTT"));
        assertThat(received.getGenotype(), is(Genotype.HETEROZYGOUS));
        assertThat(received.getVariantClass(), is("splicing"));
        assertThat(received.getPathomechanism(), is("splicing|3ss|disrupted"));

        VariantValidation validation = received.getVariantValidation();
        assertThat(validation.getContext(), is(VariantValidation.Context.MENDELIAN));
        assertThat(validation.getComparability(), is(true));

    }

}