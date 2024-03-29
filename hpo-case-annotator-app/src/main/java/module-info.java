module org.monarchinitiative.hca.app {
    requires org.monarchinitiative.hca.io;
    requires org.monarchinitiative.hca.forms;
    requires org.monarchinitiative.hca.convert;
    requires org.monarchinitiative.hca.export;

    requires org.monarchinitiative.phenol.io;
    requires org.monarchinitiative.fenominal.core;

    requires org.phenopackets.phenopackettools.core; // TODO should be redundant with phenopacket-tools v1.0.0-RC2 or better
    requires org.phenopackets.phenopackettools.io;

    requires jannovar.core;
    requires htsjdk;

    requires spring.boot;
    requires spring.context;
    requires spring.boot.autoconfigure;

    requires org.apache.commons.compress;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    requires org.slf4j;


    exports org.monarchinitiative.hpo_case_annotator.app to javafx.graphics;
    exports org.monarchinitiative.hpo_case_annotator.app.config;
    exports org.monarchinitiative.hpo_case_annotator.app.controller to javafx.fxml;
    exports org.monarchinitiative.hpo_case_annotator.app.model;
    exports org.monarchinitiative.hpo_case_annotator.app.model.genome;
    exports org.monarchinitiative.hpo_case_annotator.app.publication;

    opens org.monarchinitiative.hpo_case_annotator.app;
    opens org.monarchinitiative.hpo_case_annotator.app.config;
    opens org.monarchinitiative.hpo_case_annotator.app.controller;
    opens org.monarchinitiative.hpo_case_annotator.app.model to javafx.base;
    opens org.monarchinitiative.hpo_case_annotator.app.model.genome to javafx.base;

}