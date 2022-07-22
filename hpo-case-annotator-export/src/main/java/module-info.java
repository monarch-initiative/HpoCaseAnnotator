module org.monarchinitiative.hca.export {
    requires transitive org.monarchinitiative.hca.model;

    requires transitive org.phenopackets.schema;
    requires org.phenopackets.phenopackettools.builder;

    requires org.apache.commons.codec;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.export;
}