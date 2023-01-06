package org.monarchinitiative.hpo_case_annotator.forms;

/**
 * Implementors provide {@link DataEdit} protocol where the initial data is edited in an application-modal dialog
 * and the changes are committed by calling {@link #commit()} or discarded by <em>not</em> calling {@link #commit()}.
 *
 * @param <T> data type
 */
public interface DataEdit<T> {

    void setInitialData(T data);

    void commit();
}
