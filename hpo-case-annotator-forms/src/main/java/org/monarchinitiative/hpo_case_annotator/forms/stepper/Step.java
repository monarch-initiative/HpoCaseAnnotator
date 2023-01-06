package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;

public interface Step<T> extends ObservableDataComponent<T> {

    Parent getContent();

}
