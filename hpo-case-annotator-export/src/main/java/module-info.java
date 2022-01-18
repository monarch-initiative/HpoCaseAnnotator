module org.monarchinitiative.hca.export {
    requires org.monarchinitiative.hca.convert;
    requires org.monarchinitiative.hca.io;
    requires org.monarchinitiative.hca.core;

    requires transitive phenopacket.schema;

    requires org.apache.commons.codec;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.export;
}