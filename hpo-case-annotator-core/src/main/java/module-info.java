module org.monarchinitiative.hpo_case_annotator.core {
    requires org.monarchinitiative.hpo_case_annotator.model;
    requires org.slf4j;
    requires transitive htsjdk; // TODO - change liftover signature such that we do not work with HtsJDK intervals

    exports org.monarchinitiative.hpo_case_annotator.core.io;
    exports org.monarchinitiative.hpo_case_annotator.core.liftover;
    exports org.monarchinitiative.hpo_case_annotator.core.publication;
    exports org.monarchinitiative.hpo_case_annotator.core.refgenome;
    exports org.monarchinitiative.hpo_case_annotator.core.utils;
    exports org.monarchinitiative.hpo_case_annotator.core.validation;
}