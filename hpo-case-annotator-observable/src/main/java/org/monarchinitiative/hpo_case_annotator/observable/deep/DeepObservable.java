package org.monarchinitiative.hpo_case_annotator.observable.deep;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import org.monarchinitiative.hpo_case_annotator.observable.util.ListenerHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Base class for {@link Observable} entities have one or more {@link ObjectProperty} that holds {@link Observable}
 * entity. {@link DeepObservable} implements {@link Observable} and ensures all listeners are notified about changes to
 * <em>all</em> properties, including {@link Observable} content of {@link ObjectProperty}.
 * <p>
 * Created on 9/10/2022
 *
 * @author ielis
 */
public abstract class DeepObservable extends ObservableItem {

    private final ListenerHelper helper;
    /*
     * We can't run `initialize()` in the constructor since it works with `objectProperties()` which
     * have not yet been set at the time of the call. As a workaround, we check `unitialized` and
     */
    private final AtomicBoolean uninitialized = new AtomicBoolean(true);

    protected DeepObservable() {
        this.helper = ListenerHelper.of();
    }

    /**
     * @return {@link Stream} of {@link ObjectProperty} instances that hold {@link Observable} items.
     */
    protected abstract Stream<Property<? extends Observable>> objectProperties();

    @Override
    public void addListener(InvalidationListener listener) {
        initialize();
        // Attach the listener to observables.
        observables().forEach(obs -> obs.addListener(listener));
        // Attach the listener to the non-null Observable values currently set to ObjectProperties.
        objectProperties().forEach(obs -> {
            Observable value = obs.getValue();
            if (value != null)
                value.addListener(listener);
        });
        // Keep track of the listeners
        helper.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        initialize();
        // Remove the listener from the observables.
        observables().forEach(obs -> obs.removeListener(listener));
        // Remove the listener from the non-null Observable values currently set to ObjectProperties.
        objectProperties().forEach(obs -> {
            Observable value = obs.getValue();
            if (value != null) {
                value.removeListener(listener);
            }
        });
        // Forget the listener
        helper.removeListener(listener);
    }

    private void initialize() {
        if (uninitialized.getAndSet(false)) {
            objectProperties().forEach(
                    property -> property.addListener(
                            (obs, old, novel) -> {
                                for (InvalidationListener listener : helper.listeners()) {
                                    if (old != null) old.removeListener(listener);
                                    if (novel != null) novel.addListener(listener);
                                }
                            }));
        }
    }
}
