module org.monarchinitiative.hpo_case_annotator.io {
    requires transitive org.monarchinitiative.hpo_case_annotator.model;

    requires protobuf.java.util;
    requires com.fasterxml.jackson.databind;

    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.io;
}