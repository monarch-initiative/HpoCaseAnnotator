module org.monarchinitiative.hca.observable {
    requires transitive org.monarchinitiative.hca.model;
    requires transitive javafx.base;

    exports org.monarchinitiative.hpo_case_annotator.observable.v2;
    // Let's see if we need it anywhere else.
    exports org.monarchinitiative.hpo_case_annotator.observable.deep to org.monarchinitiative.hca.app;
}