package org.monarchinitiative.hpo_case_annotator.forms;

/**
 * The implementors have {@link StudyResources}.
 */
public interface StudyResourcesAware {

    StudyResources getStudyResources();

    default void bindStudyResources(StudyResourcesAware other) {
        bind(this, other);
    }

    private static void bind(StudyResourcesAware source, StudyResourcesAware target) {
        source.getStudyResources().hpoProperty().bind(target.getStudyResources().hpoProperty());
        source.getStudyResources().diseaseIdentifierServiceProperty().bind(target.getStudyResources().diseaseIdentifierServiceProperty());
        source.getStudyResources().functionalAnnotationRegistryProperty().bind(target.getStudyResources().functionalAnnotationRegistryProperty());
        source.getStudyResources().genomicAssemblyRegistryProperty().bind(target.getStudyResources().genomicAssemblyRegistryProperty());
    }
}
