package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.forms.base.HBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.TimeElementUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.util.Map;

import static javafx.beans.binding.Bindings.*;

public class TimeElementSummary extends HBoxBindingObservableDataComponent<ObservableTimeElement> {

    private static final Map<TimeElement.TimeElementCase, Image> ICONS = TimeElementIconUtil.loadIcons();

    @FXML
    private ImageView icon;
    @FXML
    private Label summary;
    private ObjectBinding<Image> iconBinding;
    private InvalidationListener summaryListener;

    public TimeElementSummary() {
        FXMLLoader loader = new FXMLLoader(TimeElementSummary.class.getResource("TimeElementSummary.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        ObjectBinding<TimeElement.TimeElementCase> timeElementCaseBinding = select(data, "timeElementCase");
        iconBinding = createObjectBinding(
                () -> data.get() == null
                        ? null
                        : ICONS.get(data.get().getTimeElementCase()),
                timeElementCaseBinding);
        summaryListener = obs -> summary.setText(TimeElementUtils.summarizeObservableTimeElement(data.get()));
    }

    @Override
    protected void bind(ObservableTimeElement data) {
        if (data != null) {
            icon.imageProperty().bind(iconBinding);
            data.addListener(summaryListener);
        } else {
            icon.setImage(null);
            summary.setText(TimeElementUtils.NA);
        }
    }

    @Override
    protected void unbind(ObservableTimeElement data) {
        icon.imageProperty().unbind();
        if (data != null)
            data.removeListener(summaryListener);
    }
}
