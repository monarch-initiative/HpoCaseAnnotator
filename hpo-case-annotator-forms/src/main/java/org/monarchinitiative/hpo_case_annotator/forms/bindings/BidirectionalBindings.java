package org.monarchinitiative.hpo_case_annotator.forms.bindings;


import javafx.beans.property.ObjectProperty;

import java.util.function.Function;

public class BidirectionalBindings {

    private static <A, B> BidirectionalBinding<A, B> bindBidirectional(ObjectProperty<A> a,
                                                                       ObjectProperty<B> b,
                                                                       Function<A, B> aToB,
                                                                       Function<B, A> bToA) {
        return new BidirectionalBinding<>(a, b, aToB, bToA);
    }
}
