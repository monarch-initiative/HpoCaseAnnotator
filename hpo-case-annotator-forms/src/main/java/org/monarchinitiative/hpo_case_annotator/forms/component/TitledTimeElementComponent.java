package org.monarchinitiative.hpo_case_annotator.forms.component;

import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;

import java.util.List;

public class TitledTimeElementComponent extends TitledBase<TimeElementComponent> {

    private static final List<String> STYLECLASSES = List.of("tl-time-element-component");

    @Override
    protected TimeElementComponent getItem() {
        return new TimeElementComponent();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }
}
