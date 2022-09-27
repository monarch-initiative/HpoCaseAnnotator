package org.monarchinitiative.hpo_case_annotator.core.mining;

import java.util.List;

/**
 * The implementors perform named entity recognition (e.g. identification of HPO terms) from a free text.
 */
public interface NamedEntityFinder {

    /**
     * Find a {@link List} of {@link MinedTerm}s in the {@code source} text.
     */
    List<MinedTerm> process(String source);

}
