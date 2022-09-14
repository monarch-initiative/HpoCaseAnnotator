package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.binding.StringBinding;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.forms.util.TimeElementUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.util.Map;

public class ObservableTimeElementTableCell<T> extends TableCell<T, ObservableTimeElement> {

    private static final Map<TimeElement.TimeElementCase, Image> ICONS = TimeElementIconUtil.loadIcons();

    private final ImageView imageView;

    public ObservableTimeElementTableCell() {
        setContentDisplay(ContentDisplay.LEFT);
        imageView = new ImageView();
        imageView.setFitHeight(20.);
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }

    @Override
    protected void updateItem(ObservableTimeElement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            imageView.setImage(null);

            textProperty().unbind();
            setText(null);
        } else {
            item.timeElementCaseProperty().addListener((obs, old, novel) -> imageView.setImage(ICONS.get(novel)));
            textProperty().bind(summarizeTimeElement(item));
        }
    }

    private static StringBinding summarizeTimeElement(ObservableTimeElement item) {

        return new StringBinding() {
            {
                bind(item);
            }

            @Override
            protected String computeValue() {
                return TimeElementUtils.summarizeObservableTimeElement(item);
            }
        };
    }

}
