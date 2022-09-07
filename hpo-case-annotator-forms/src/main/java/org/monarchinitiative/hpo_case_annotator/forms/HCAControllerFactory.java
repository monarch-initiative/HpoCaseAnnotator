package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.util.Callback;

/**
 * Marker interface.
 * @deprecated in favor of providing functional dependencies via property binding mechanism.
 */
@Deprecated(forRemoval = true, since = "2.0.0")
public interface HCAControllerFactory extends Callback<Class<?>, Object> {

}
