module org.monarchinitiative.hpo_case_annotator.export {
    requires org.monarchinitiative.hpo_case_annotator.convert;
    requires org.monarchinitiative.hpo_case_annotator.io;
    requires org.monarchinitiative.hpo_case_annotator.core;

    requires phenopacket.schema;
    requires protobuf.java;
    requires org.apache.commons.codec;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.export;
}