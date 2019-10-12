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
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class SomaticVariantControllerTest extends ApplicationTest {


    @Inject
    private Injector injector;

    private SomaticVariantController controller;

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
        FXMLLoader loader = new FXMLLoader(SomaticVariantController.class.getResource("SomaticVariant.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * The minimal information for variant to be valid is
     * <ul>
     * <li>genome build</li>
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
        assertThat(controller.isComplete(), is(false));

        clickOn("#genomeBuildComboBox")
                .moveBy(0, 40)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isComplete(), is(false));

        clickOn("#chromosomeComboBox")
                .moveBy(0, 30)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isComplete(), is(false));

        clickOn("#positionTextField")
                .write("12345345");
        assertThat(controller.isComplete(), is(false));

        clickOn("#referenceTextField")
                .write("C");
        assertThat(controller.isComplete(), is(false));

        clickOn("#alternateTextField")
                .write("A");
        assertThat(controller.isComplete(), is(false));

        clickOn("#snippetTextField")
                .write("CACT[C/A]AACCG");
        assertThat(controller.isComplete(), is(false));

        clickOn("#genotypeComboBox")
                .moveBy(0, 60)
                .clickOn(MouseButton.PRIMARY);
        assertThat(controller.isComplete(), is(true));

        // assert
        Variant variant = controller.getData();
        VariantPosition variantPosition = variant.getVariantPosition();
        assertThat(variantPosition.getGenomeAssembly(), is(GenomeAssembly.GRCH_38));
        assertThat(variantPosition.getContig(), is("1"));
        assertThat(variantPosition.getPos(), is(12345345));
        assertThat(variantPosition.getRefAllele(), is("C"));
        assertThat(variantPosition.getAltAllele(), is("A"));
        assertThat(variant.getSnippet(), is("CACT[C/A]AACCG"));
        assertThat(variant.getGenotype(), is(Genotype.HETEROZYGOUS));
    }


    @Test
    public void getIncompleteVariant() {
        // we did not set any data, hence the variant is incomplete
//        assertThat(controller.getData(), is(equalTo(Variant.getDefaultInstance()))); // TODO - implement
    }

    @Test
    public void presentAndGetTheSameData() {
        final Variant presented = DiseaseCaseModelExample.makeSomaticVariant();

        Platform.runLater(() -> controller.presentData(presented));
        sleep(50, TimeUnit.MILLISECONDS);
        final Variant received = controller.getData();

        assertThat(received, is(equalTo(presented)));
    }


}