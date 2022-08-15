module org.monarchinitiative.hca.forms {
    requires transitive org.monarchinitiative.hca.core;
    requires transitive org.monarchinitiative.hpo_case_annotator.observable;

    // TODO - figure out transitivity of JavaFX modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    requires spring.context;

    requires org.slf4j;

    exports org.monarchinitiative.hpo_case_annotator.forms;
    exports org.monarchinitiative.hpo_case_annotator.forms.component;
    exports org.monarchinitiative.hpo_case_annotator.forms.component.age;
    exports org.monarchinitiative.hpo_case_annotator.forms.liftover;
    exports org.monarchinitiative.hpo_case_annotator.forms.metadata;
    exports org.monarchinitiative.hpo_case_annotator.forms.nvo;
    exports org.monarchinitiative.hpo_case_annotator.forms.pedigree;
    exports org.monarchinitiative.hpo_case_annotator.forms.phenotype;
    exports org.monarchinitiative.hpo_case_annotator.forms.publication;
    exports org.monarchinitiative.hpo_case_annotator.forms.stepper;
    exports org.monarchinitiative.hpo_case_annotator.forms.variants;

    exports org.monarchinitiative.hpo_case_annotator.forms.v2;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.disease;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.individual;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.model;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.variant;


    opens org.monarchinitiative.hpo_case_annotator.forms to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.component to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.component.age to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.liftover to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.mining to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.metadata to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.nvo to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.pedigree to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.phenotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.publication to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variants to javafx.fxml, org.monarchinitiative.hca.app;

    opens org.monarchinitiative.hpo_case_annotator.forms.v2 to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.disease to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.model to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.variant to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.util to javafx.fxml;
}