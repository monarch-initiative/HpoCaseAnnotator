package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.property.ObjectProperty;

public interface GenomicResources<T> {

    ObjectProperty<T> hg19Property();

    ObjectProperty<T> hg38Property();

    // ******************************************** DEFAULT METHODS ************************************************* //

    default T getHg19() {
        return hg19Property().get();
    }

    default void setHg19(T hg19) {
        hg19Property().set(hg19);
    }

    default T getHg38() {
        return hg38Property().get();
    }

    default void setHg38(T hg38) {
        hg38Property().set(hg38);
    }

}
