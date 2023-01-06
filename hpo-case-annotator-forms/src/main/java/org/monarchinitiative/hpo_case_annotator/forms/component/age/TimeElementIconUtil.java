package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.scene.image.Image;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class TimeElementIconUtil {

    static Map<TimeElement.TimeElementCase, Image> loadIcons() {
        try {
            return Map.of(
                    TimeElement.TimeElementCase.GESTATIONAL_AGE, loadImage("pregnant.png"),
                    TimeElement.TimeElementCase.AGE, loadImage("person.png"),
                    TimeElement.TimeElementCase.AGE_RANGE, loadImage("person.png"),
                    TimeElement.TimeElementCase.ONTOLOGY_CLASS, loadImage("hpo-logo-black.png")
            );
        } catch (IOException e) {

            return Map.of();
        }
    }

    private static Image loadImage(String name) throws IOException {
        try (InputStream is = ObservableTimeElementTableCell.class.getResourceAsStream(name)) {
            return new Image(is);
        }
    }

}
