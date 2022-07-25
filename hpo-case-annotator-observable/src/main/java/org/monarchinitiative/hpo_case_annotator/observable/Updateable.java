package org.monarchinitiative.hpo_case_annotator.observable;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface Updateable<T> {

    <U extends T> void update(U data);

    /**
     * Update {@code target} with elements from {@code source} and ensure that the {@code target} has the same
     * number of elements as the {@code source} has.
     *
     * @param source   a list with source elements.
     * @param target   a list of target elements.
     * @param supplier supplier of new instances of {@link T} used if {@code source} has more elements than {@code target}.
     */
    static <S, T extends Updateable<S>> void updateObservableList(List<? extends S> source,
                                                                  ObservableList<T> target,
                                                                  Supplier<? extends T> supplier) {
        int upTo = Math.min(source.size(), target.size());
        // Sync the elements
        for (int i = 0; i < upTo; i++) {
            target.get(i).update(source.get(i));
        }
        if (source.size() < upTo) {
            // Source has fewer elements than target, so truncate the target.
            target.remove(upTo, target.size());
        } else {
            // Source has equal or more elements than target, so let's append the outstanding elements into target.
            for (int i = upTo; i < source.size(); i++) {
                T T = supplier.get();
                T.update(source.get(i));
                target.add(T);
            }
        }

    }

    /**
     * Update {@code target} with entries from {@code source} and ensure that the {@code target} has the same
     * number of entries as the {@code source} has.
     *
     * @param source   a list with source entries.
     * @param target   a list of target entries.
     * @param supplier supplier of new instances of {@link T} used if the value is present in {@code source} but absent
     *                 from {@code target}.
     */
    static <K, S, T extends Updateable<S>> void updateObservableMap(Map<K, S> source,
                                                                    ObservableMap<K, T> target,
                                                                    Supplier<T> supplier) {
        // First, remove the key/value pairs that are not in the source.
        for (K key : target.keySet()) {
            if (!source.containsKey(key))
                target.remove(key);
        }
        // Then, add/update all elements that are new/present in the target.
        for (Map.Entry<? extends K, ? extends S> entry : source.entrySet()) {
            K key = entry.getKey();
            T item = target.get(key);

            if (item == null)
                // Add new item
                target.put(key, supplier.get());
            else
                // Update existing item
                item.update(entry.getValue());
        }
    }


    static <T, U extends Updateable<T>> U update(U updateable, T data) {
        updateable.update(data);
        return updateable;
    }
}
