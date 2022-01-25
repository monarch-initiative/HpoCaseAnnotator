module org.monarchinitiative.hca.core {
    requires transitive org.monarchinitiative.hca.model;

    requires htsjdk;
    requires com.google.protobuf;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.core.data;
    exports org.monarchinitiative.hpo_case_annotator.core.io;
    exports org.monarchinitiative.hpo_case_annotator.core.liftover;
    exports org.monarchinitiative.hpo_case_annotator.core.publication;
    exports org.monarchinitiative.hpo_case_annotator.core.reference;
    exports org.monarchinitiative.hpo_case_annotator.core.validation;
    exports org.monarchinitiative.hpo_case_annotator.core.reference.genome;
}