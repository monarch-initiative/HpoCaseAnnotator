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
import org.monarchinitiative.hpo_case_annotator.gui.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.gui.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.gui.PojosForTesting;
import org.monarchinitiative.hpo_case_annotator.gui.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class IntrachromosomalVariantControllerTest extends ApplicationTest {

    @Inject
    private Injector injector;

    private IntrachromosomalVariantController controller;

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

    @Test
    public void presentAndGetTheSameData() {
        final Variant presented = PojosForTesting.makeStructuralDeletionVariant();

        Platform.runLater(() -> controller.presentData(presented));
        sleep(30, TimeUnit.MILLISECONDS);
        final Variant received = controller.getData();

        assertThat(received, is(equalTo(presented)));
    }

    @Test
    public void enterData() {
        // genome build
        clickOn("#assemblyComboBox").moveBy(0, 80).clickOn(MouseButton.PRIMARY)
                // chromosome
                .clickOn("#chromosomeComboBox").moveBy(0, 60).clickOn(MouseButton.PRIMARY)
                // begin
                .clickOn("#beginTextField").write("100")
                // end
                .clickOn("#endTextField").write("200")
                // genotype
                .clickOn("#genotypeComboBox").moveBy(0, 70).clickOn(MouseButton.PRIMARY)
                // sv type
                .clickOn("#svTypeComboBox").moveBy(0, 90).clickOn(MouseButton.PRIMARY)
                // imprecise
                .clickOn("#preciseCheckBox")
                // ci begin first
                .clickOn("#ciBeginFirstTextField").write("-5")
                // ci begin second
                .clickOn("#ciBeginSecondTextField").write("10")
                // ci end first
                .clickOn("#ciEndFirstTextField").write("-15")
                // ci end second
                .clickOn("#ciEndSecondTextField").write("20")
                // cosegregation
                .clickOn("#cosegregationCheckBox");

        Variant actual = controller.getData();

        assertThat(actual.getVariantPosition(), is(VariantPosition.newBuilder()
                .setGenomeAssembly(GenomeAssembly.GRCH_38)
                .setContig("4")
                .setPos(100)
                .setPos2(200)
                .setRefAllele("N")
                .setAltAllele("<INS>")
                .setCiBeginOne(-5)
                .setCiBeginTwo(10)
                .setCiEndOne(-15)
                .setCiEndTwo(20)
                .build()));
        assertThat(actual.getVariantClass(), is("structural"));
        assertThat(actual.getGenotype(), is(Genotype.HOMOZYGOUS_ALTERNATE));
        assertThat(actual.getSvType(), is(StructuralType.INS));
        assertThat(actual.getVariantValidation(), is(VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.INTRACHROMOSOMAL)
                .setCosegregation(true)
                .build()));
        assertThat(actual.getImprecise(), is(true));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MendelianVariantController.class.getResource("IntrachromosomalVariant.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }
}