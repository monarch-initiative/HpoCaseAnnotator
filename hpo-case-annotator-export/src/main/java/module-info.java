module org.monarchinitiative.hca.export {
    requires transitive org.monarchinitiative.hca.model;

    requires transitive phenopacket.schema;
    requires org.phenopacket.tools.builder;

    requires org.apache.commons.codec;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.export;
}