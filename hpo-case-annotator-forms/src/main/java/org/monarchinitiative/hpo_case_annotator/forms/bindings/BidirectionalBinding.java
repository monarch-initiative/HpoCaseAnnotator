package org.monarchinitiative.hpo_case_annotator.forms.bindings;

import javafx.beans.WeakListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

public final class BidirectionalBinding<A, B> implements WeakListener {

    private final WeakReference<ObjectProperty<A>> aRef;
    private final WeakReference<ObjectProperty<B>> bRef;
    private final Function<A, B> aToB;
    private final Function<B, A> bToA;
    private final int cachedHashCode;
    private ChangeListener<A> aListener;
    private ChangeListener<B> bListener;

    private boolean updating;

    BidirectionalBinding(ObjectProperty<A> a,
                         ObjectProperty<B> b,
                         Function<A, B> aToB,
                         Function<B, A> bToA) {
        this.aRef = new WeakReference<>(a);
        this.bRef = new WeakReference<>(b);

        this.aToB = Objects.requireNonNull(aToB, "A to B must not be null");
        this.bToA = Objects.requireNonNull(bToA, "B to A must not be null");

        setupListeners(a, b);

        this.cachedHashCode = a.hashCode() * b.hashCode();
    }

    private void setupListeners(ObjectProperty<A> a, ObjectProperty<B> b) {
        aListener = createListener(aRef.get(), aListener, bRef.get(), bListener, aToB);
        a.addListener(aListener);
        bListener = createListener(bRef.get(), bListener, aRef.get(), aListener, bToA);
        b.addListener(bListener);
    }

    private <T, U> ChangeListener<T> createListener(ObjectProperty<T> t,
                                                    ChangeListener<T> tListener,
                                                    ObjectProperty<U> u,
                                                    ChangeListener<U> uListener,
                                                    Function<T, U> tToU) {
        return (obs, old, novel) -> {
            if (!updating) {
                if ((t == null) || (u == null)) {
                    unbind(t, u, tListener, uListener);
                } else {
                    try {
                        updating = true;
                        if (t == obs) {
                            u.set(tToU.apply(novel));
                        } else {
                            t.set(novel);
                        }
                    } catch (RuntimeException e) {
                        try {
                            // try to set the old values
                            if (t == obs) {
                                t.set(old);
                            } else {
                                u.set(tToU.apply(old));
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            t.removeListener(tListener);
                            u.removeListener(uListener);
                            throw new RuntimeException(
                                    "Bidirectional binding failed together with an attempt"
                                            + " to restore the source property to the previous value."
                                            + " Removing the bidirectional binding from properties " +
                                            t + " and " + u, e2);
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        };
    }

    private static <A, B> void unbind(ObjectProperty<A> a,
                                      ObjectProperty<B> b,
                                      ChangeListener<A> aListener,
                                      ChangeListener<B> bListener) {
        if (a != null) {
            a.removeListener(aListener);
        }
        if (b != null) {
            b.removeListener(bListener);
        }
    }

    public void unbind() {
        unbind(aRef.get(), bRef.get(), aListener, bListener);
    }

    @Override
    public boolean wasGarbageCollected() {
        // TODO - does this work?
        return aRef.get() == null || bRef.get() == null;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        Object thisA = a();
        Object thisB = b();
        if ((thisA == null) || (thisB == null)) {
            return false;
        }

        if (obj instanceof BidirectionalBinding<?, ?> otherBinding) {
            final Object otherA = otherBinding.a();
            final Object otherB = otherBinding.b();
            if ((otherA == null) || (otherB == null)) {
                return false;
            }

            if (thisA == otherA && thisB == otherB) {
                return true;
            }
            return thisA == otherB && thisB == otherA;
        }
        return false;
    }

    public ObjectProperty<A> a() {
        return aRef.get();
    }

    public ObjectProperty<B> b() {
        return bRef.get();
    }
}
