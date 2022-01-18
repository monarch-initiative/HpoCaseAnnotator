module org.monarchinitiative.hca.core {
    requires transitive org.monarchinitiative.hca.model;
    requires transitive htsjdk; // TODO - change liftover signature such that we do not work with HtsJDK intervals

    requires com.google.protobuf;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.core.io;
    exports org.monarchinitiative.hpo_case_annotator.core.liftover;
    exports org.monarchinitiative.hpo_case_annotator.core.publication;
    exports org.monarchinitiative.hpo_case_annotator.core.reference;
    exports org.monarchinitiative.hpo_case_annotator.core.utils;
    exports org.monarchinitiative.hpo_case_annotator.core.validation;
}