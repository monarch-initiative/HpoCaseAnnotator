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
        StudyResources s = source.getStudyResources();
        StudyResources t = target.getStudyResources();
        t.hpoProperty().bind(s.hpoProperty());
        t.diseaseIdentifierServiceProperty().bind(s.diseaseIdentifierServiceProperty());
        t.functionalAnnotationRegistryProperty().bind(s.functionalAnnotationRegistryProperty());
        t.genomicAssemblyRegistryProperty().bind(s.genomicAssemblyRegistryProperty());
    }
}
