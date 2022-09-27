module org.monarchinitiative.hca.core {
    requires transitive org.monarchinitiative.hca.model;
    requires transitive org.monarchitiative.svart;

    requires htsjdk;
    requires jannovar.core;
    requires com.google.common;
    requires com.google.protobuf;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.core.data;
    exports org.monarchinitiative.hpo_case_annotator.core.io;
    exports org.monarchinitiative.hpo_case_annotator.core.liftover;
    exports org.monarchinitiative.hpo_case_annotator.core.mining;
    exports org.monarchinitiative.hpo_case_annotator.core.publication;
    exports org.monarchinitiative.hpo_case_annotator.core.reference;
    exports org.monarchinitiative.hpo_case_annotator.core.validation;
    exports org.monarchinitiative.hpo_case_annotator.core.reference.functional;
    exports org.monarchinitiative.hpo_case_annotator.core.reference.genome;
    exports org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted; // TODO - remove
}