module org.monarchinitiative.hpo_case_annotator.gui {
    requires org.monarchinitiative.hpo_case_annotator.core;
    requires org.monarchinitiative.hpo_case_annotator.convert;
    requires org.monarchinitiative.hpo_case_annotator.export;
    requires org.monarchinitiative.hpo_case_annotator.io;
    requires org.monarchinitiative.hpo_case_annotator.forms;

    requires org.monarchinitiative.hpotextmining.gui;
    requires org.monarchinitiative.phenol.io;

    requires com.google.protobuf.util;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.controlsfx.controls;

    requires com.google.guice; // TODO - deprecate/remove

    requires javax.inject;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.google.common; // TODO - deprecate/remove

    requires htsjdk;
    requires org.apache.commons.compress;
//    requires org.apache.commons.io;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.gui;
//    exports org.monarchinitiative.hpo_case_annotator.gui to javafx.graphics;
    exports org.monarchinitiative.hpo_case_annotator.gui.controllers;
//    exports org.monarchinitiative.hpo_case_annotator.gui.controllers to javafx.fxml;
    exports org.monarchinitiative.hpo_case_annotator.gui.util;

    opens org.monarchinitiative.hpo_case_annotator.gui.controllers to javafx.fxml, com.google.guice, com.fasterxml.jackson.databind;
    opens org.monarchinitiative.hpo_case_annotator.gui to com.google.guice; // TODO - deprecate/remove
}