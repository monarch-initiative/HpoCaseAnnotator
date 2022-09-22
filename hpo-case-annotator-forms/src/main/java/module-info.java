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
    exports org.monarchinitiative.hpo_case_annotator.forms.liftover;
    exports org.monarchinitiative.hpo_case_annotator.forms.status;
    exports org.monarchinitiative.hpo_case_annotator.forms.study;
    exports org.monarchinitiative.hpo_case_annotator.forms.stepper;
    // TODO - remove `variants` packages from the API after removing `gui` module
    exports org.monarchinitiative.hpo_case_annotator.forms.variants;
    exports org.monarchinitiative.hpo_case_annotator.forms.variants.input;

    exports org.monarchinitiative.hpo_case_annotator.forms.v2;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.disease;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.individual;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.model;
    exports org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

    opens org.monarchinitiative.hpo_case_annotator.forms.base to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.component to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.component.age to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.disease to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.liftover to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.mining to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.metadata to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.pedigree to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.phenotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.publication to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.status to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.study to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.disease to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.genotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.tree to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variants to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variants.detail to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.variants.input to javafx.fxml;

    // TODO - remove the `v2` package
    opens org.monarchinitiative.hpo_case_annotator.forms.v2 to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.disease to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.individual to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.model to javafx.fxml;
    opens org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype to javafx.fxml, org.monarchinitiative.hca.app;
    opens org.monarchinitiative.hpo_case_annotator.forms.util to javafx.fxml;
}