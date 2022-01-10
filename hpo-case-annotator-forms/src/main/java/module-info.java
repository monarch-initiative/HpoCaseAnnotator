module org.monarchinitiative.hpo_case_annotator.forms {
    requires org.monarchinitiative.hpo_case_annotator.core;
    requires org.monarchinitiative.hpo_case_annotator.test; // TODO remove after prototyping

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.forms;
    exports org.monarchinitiative.hpo_case_annotator.forms.model;
    exports org.monarchinitiative.hpo_case_annotator.forms.individual;
    exports org.monarchinitiative.hpo_case_annotator.forms.variant;

    opens org.monarchinitiative.hpo_case_annotator.forms to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.model to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.ontotree to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.phenotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variant to javafx.fxml;
}