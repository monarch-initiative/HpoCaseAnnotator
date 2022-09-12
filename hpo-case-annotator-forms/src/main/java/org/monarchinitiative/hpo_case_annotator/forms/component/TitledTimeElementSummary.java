package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.util.List;

public class TitledTimeElementSummary extends TitledBase<TimeElementSummary> {

    private static final List<String> STYLECLASSES = List.of("tl-time-element-summary");

    @Override
    protected TimeElementSummary getItem() {
        return new TimeElementSummary();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public ObjectProperty<ObservableTimeElement> timeElementProperty() {
        return item.dataProperty();
    }
}
