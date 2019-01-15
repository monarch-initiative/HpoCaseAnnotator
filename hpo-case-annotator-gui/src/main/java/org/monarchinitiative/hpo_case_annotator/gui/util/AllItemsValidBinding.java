package org.monarchinitiative.hpo_case_annotator.gui.util;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is a subclass of {@link BooleanBinding} that observes an {@link ObservableList} with instances of {@link T} class.
 * The binding evaluates to <code>true</code> if all the instances match specified <code>condition</code>.
 * A <b>purpose</b> and motivation for writing this class was to be able to observe validity of a collection of instances
 * of a certain class. E.g. in {@link DiseaseCaseDataController} the HpoCaseAnnotator
 * observes validity/completeness of multiple {@link org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.AbstractVariantController}s.
 * Then, the binding represented by this class will evaluate to true, if all the variants are valid/complete. Variants
 * can be added/removed at runtime.
 * <p>
 * In order to evaluate the binding you need to provide an <code>extractor</code> function that will be used to extract
 * {@link Observable} from an instance of {@link T}. Also, a {@link Predicate} for {@link Observable} must be provided
 * for evaluation of the binding.
 *
 * @param <T> class of observed items
 * @param <O> subclass of {@link Observable} that is derived from the {@link T} using <code>extractor</code> function
 */
@Deprecated
public class AllItemsValidBinding<T, O extends Observable> extends BooleanBinding {

    private final ObservableList<T> items;

    private final Function<T, O> extractor;

    private final Predicate<O> condition;

    public AllItemsValidBinding(ObservableList<T> items, Function<T, O> extractor, Predicate<O> condition) {
        this.items = items;
        this.extractor = extractor;
        this.condition = condition;

        items.addListener((InvalidationListener) c -> invalidate());
        items.addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (T e : c.getAddedSubList()) {
                        addBinding(extractor.apply(e));
                    }
                } else if (c.wasRemoved()) {
                    for (T e : c.getRemoved()) {
                        removeBinding(extractor.apply(e));
                    }
                } else {
                    System.out.println("WHOA");
                }
            }
        });
    }

    private void addBinding(Observable... bp) {
        bind(bp);
    }

    private void removeBinding(Observable... bp) {
        unbind(bp);
    }

    @Override
    protected boolean computeValue() {
        return items.stream().map(extractor).allMatch(condition);
    }


}
