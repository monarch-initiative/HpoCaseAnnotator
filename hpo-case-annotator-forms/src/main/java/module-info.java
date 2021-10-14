module org.monarchinitiative.hpo_case_annotator.forms {
    requires transitive org.monarchinitiative.hpo_case_annotator.model;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    exports org.monarchinitiative.hpo_case_annotator.forms;
    exports org.monarchinitiative.hpo_case_annotator.forms.utils;

    opens org.monarchinitiative.hpo_case_annotator.forms.individual;
}