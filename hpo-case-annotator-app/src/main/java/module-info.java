module org.monarchinitiative.hpo_case_annotator.app {
    requires org.monarchinitiative.hpo_case_annotator.core;
    requires org.monarchinitiative.hpo_case_annotator.io;
    requires org.monarchinitiative.hpo_case_annotator.forms;
    requires org.monarchinitiative.hpo_case_annotator.convert;

    requires org.monarchinitiative.phenol.io;

    requires spring.boot;
    requires spring.context;
    requires spring.boot.autoconfigure;

    requires org.apache.commons.compress;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;


    exports org.monarchinitiative.hpo_case_annotator.app to javafx.graphics;
    exports org.monarchinitiative.hpo_case_annotator.app.config;
    exports org.monarchinitiative.hpo_case_annotator.app.controller to javafx.fxml;
    exports org.monarchinitiative.hpo_case_annotator.app.model;
    exports org.monarchinitiative.hpo_case_annotator.app.model.genome;

    opens org.monarchinitiative.hpo_case_annotator.app;
    opens org.monarchinitiative.hpo_case_annotator.app.config;
    opens org.monarchinitiative.hpo_case_annotator.app.controller;
    opens org.monarchinitiative.hpo_case_annotator.app.model;
    opens org.monarchinitiative.hpo_case_annotator.app.model.genome;

}