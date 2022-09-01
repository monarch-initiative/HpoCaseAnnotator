package org.monarchinitiative.hpo_case_annotator.forms.component.age;


import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

@Disabled
@ExtendWith(ApplicationExtension.class)
public class TimeElementBindingComponentTest {

    private TimeElementBindingComponent component;

    @Start
    public void start(Stage stage) throws Exception {
        component = new TimeElementBindingComponent();


        Scene scene = new Scene(component);
        stage.setScene(scene);
        stage.setTitle("Phenotype view test");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws Exception {
        component.addListener(obs -> System.err.println("NOVEL COMPONENT: " + component.getData()));

        ObservableFamilyStudy familyStudy = new ObservableFamilyStudy(TestData.V2.comprehensiveFamilyStudy());
        ObservablePedigreeMember member = familyStudy.getPedigree().membersProperty().get(0);
        ObservablePhenotypicFeature pf = member.getPhenotypicFeatures().get(0);
        ObservableTimeElement onset = pf.getOnset();
        onset.addListener(obs -> System.err.println("NOVEL ONSET: " + onset));

        ObjectProperty<ObservableTimeElement> time = new SimpleObjectProperty<>();
        component.dataProperty().bind(time);
        robot.sleep(3, TimeUnit.SECONDS);
        System.err.println("Setting time");
        Platform.runLater(() -> time.set(onset));
        time.addListener((obs, old, novel) -> System.err.printf("Novel: %s%n", novel));

        robot.sleep(1, TimeUnit.MINUTES);
        System.err.println(member);
    }

}