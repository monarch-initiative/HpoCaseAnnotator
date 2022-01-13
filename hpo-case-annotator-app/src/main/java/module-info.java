module org.monarchinitiative.hpo_case_annotator.app {
    requires org.monarchinitiative.hpo_case_annotator.core;
    requires org.monarchinitiative.hpo_case_annotator.io;
    requires org.monarchinitiative.hpo_case_annotator.forms;
    requires org.monarchinitiative.hpo_case_annotator.convert;

    requires org.monarchinitiative.phenol.io;

    requires spring.boot;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires java.sql;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires org.apache.commons.compress;

    exports org.monarchinitiative.hpo_case_annotator.app to javafx.graphics;
    exports org.monarchinitiative.hpo_case_annotator.app.controller to javafx.fxml;

    opens org.monarchinitiative.hpo_case_annotator.app;
    opens org.monarchinitiative.hpo_case_annotator.app.config;
    opens org.monarchinitiative.hpo_case_annotator.app.controller;
    exports org.monarchinitiative.hpo_case_annotator.app.model to javafx.graphics;
    opens org.monarchinitiative.hpo_case_annotator.app.model;
    exports org.monarchinitiative.hpo_case_annotator.app.model.genome to javafx.graphics;
    opens org.monarchinitiative.hpo_case_annotator.app.model.genome;

}