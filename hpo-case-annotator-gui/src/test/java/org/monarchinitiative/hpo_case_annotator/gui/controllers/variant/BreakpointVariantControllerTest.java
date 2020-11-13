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
public class BreakpointVariantControllerTest extends ApplicationTest {

    @Inject
    private Injector injector;

    private BreakpointVariantController controller;


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
        final Variant presented = PojosForTesting.makeBreakendVariant();

        Platform.runLater(() -> controller.presentData(presented));
        sleep(30, TimeUnit.MILLISECONDS);
        final Variant received = controller.getData();

        assertThat(received, is(equalTo(presented)));
    }

    @Test
    public void enterData() {
        clickOn("#assemblyComboBox").moveBy(0, 80).clickOn(MouseButton.PRIMARY)
                // left chromosome
                .clickOn("#leftChrComboBox").moveBy(0, 40).clickOn(MouseButton.PRIMARY)
                // left pos
                .clickOn("#leftPosTextField").write("101")
                // left pos is on FWD strand (no-clicking)
//                .clickOn("#leftStrandCheckBox")
                // right chromosome
                .clickOn("#rightChrComboBox").moveBy(0, 80).clickOn(MouseButton.PRIMARY)
                // right pos
                .clickOn("#rightPosTextField").write("201")
                // right pos is on negative strand
                .clickOn("#rightStrandCheckBox")
                // inserted sequence
                .clickOn("#insertedSequenceTextField").write("AACCTT")
                // uncheck to imprecise
                .clickOn("#preciseCheckBox")
                // confidence intervals
                .clickOn("#ciBeginLeftTextField").write("-5")
                .clickOn("#ciEndLeftTextField").write("10")
                .clickOn("#ciBeginRightTextField").write("-15")
                .clickOn("#ciEndRightTextField").write("20");

        Variant actual = controller.getData();

        assertThat(actual.getVariantPosition(), is(VariantPosition.newBuilder()
                .setGenomeAssembly(GenomeAssembly.GRCH_38)
                .setContig("4")
                .setPos(101)
                .setContigDirection(VariantPosition.Direction.FWD)
                .setContig2("5")
                .setPos2(201)
                .setContig2Direction(VariantPosition.Direction.REV)
                .setRefAllele("")
                .setAltAllele("AACCTT")
                .setCiBeginOne(-5)
                .setCiBeginTwo(10)
                .setCiEndOne(-15)
                .setCiEndTwo(20)
                .build()));

        assertThat(actual.getVariantClass(), is("structural"));
        assertThat(actual.getGenotype(), is(Genotype.HETEROZYGOUS));
        assertThat(actual.getSvType(), is(StructuralType.BND));
        assertThat(actual.getVariantValidation(), is(VariantValidation.newBuilder()
                .setContext(VariantValidation.Context.TRANSLOCATION)
                .build()));
        assertThat(actual.getImprecise(), is(true));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(BreakpointVariantController.class.getResource("BreakpointVariant.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }
}