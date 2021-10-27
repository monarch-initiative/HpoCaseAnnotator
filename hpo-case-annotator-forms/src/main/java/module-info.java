module org.monarchinitiative.hpo_case_annotator.forms {
    requires org.monarchinitiative.hpo_case_annotator.core;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;

    exports org.monarchinitiative.hpo_case_annotator.forms;
    exports org.monarchinitiative.hpo_case_annotator.forms.model;

    opens org.monarchinitiative.hpo_case_annotator.forms to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.phenotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variant to javafx.fxml;
}