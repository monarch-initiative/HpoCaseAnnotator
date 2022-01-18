module org.monarchinitiative.hca.io {
    requires transitive org.monarchinitiative.hca.model;

    requires com.google.protobuf.util;
    requires com.fasterxml.jackson.databind;

    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.io;
}