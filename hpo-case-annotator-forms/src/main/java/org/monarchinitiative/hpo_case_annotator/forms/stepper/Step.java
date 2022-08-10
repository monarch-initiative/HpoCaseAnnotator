package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;

public interface Step<T> extends ObservableDataController<T> {

    Parent getContent();

}
