module org.monarchinitiative.hca.model {
    requires transitive org.monarchinitiative.phenol.core;
    requires transitive org.monarchitiative.svart;
    requires transitive com.google.protobuf;

    exports org.monarchinitiative.hpo_case_annotator.model;
    exports org.monarchinitiative.hpo_case_annotator.model.v2;
    exports org.monarchinitiative.hpo_case_annotator.model.v2.variant;
    exports org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

    exports org.monarchinitiative.hpo_case_annotator.model.proto;
}