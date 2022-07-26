module org.monarchinitiative.hca.forms {
    requires transitive org.monarchinitiative.hca.core;
    requires transitive org.monarchinitiative.hpo_case_annotator.observable;

    // TODO - figure out transitivity of JavaFX modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.forms;
    exports org.monarchinitiative.hpo_case_annotator.forms.nvo;
    exports org.monarchinitiative.hpo_case_annotator.forms.liftover;

    exports org.monarchinitiative.hpo_case_annotator.forms.v2;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.disease;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.individual;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.model;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.publication;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.variant;


    opens org.monarchinitiative.hpo_case_annotator.forms to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.nvo to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.liftover to javafx.fxml, org.monarchinitiative.hca.app;

    opens org.monarchinitiative.hpo_case_annotator.forms.v2 to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.disease to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.model to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.publication to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.variant to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.util to javafx.fxml;
}