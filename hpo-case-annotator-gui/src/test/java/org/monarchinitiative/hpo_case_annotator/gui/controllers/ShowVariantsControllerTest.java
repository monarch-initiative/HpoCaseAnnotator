package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.gui.PojosForTesting;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ShowVariantsControllerTest extends BaseControllerTest {

    private ShowVariantsController controller;

    @Test
    public void basic() {
        controller.setData(Collections.singleton(PojosForTesting.benMahmoud2013B3GLCT()));
        sleep(10);
        Collection<ShowVariantsController.VariantData> vd = controller.getVariantData();
        assertThat(vd.size(), is(1));
        assertThat(vd, hasItem(new ShowVariantsController.VariantData("GRCH_37 13:31843349A>G",
                "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                "23954224", "B3GLCT", "splicing",
                "splicing|3ss|disrupted", "Exon skipping")));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ShowVariantsController.class.getResource("ShowVariantsView.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }
}