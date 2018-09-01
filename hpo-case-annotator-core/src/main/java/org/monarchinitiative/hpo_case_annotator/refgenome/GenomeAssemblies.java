package org.monarchinitiative.hpo_case_annotator.refgenome;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class GenomeAssemblies {

    private final Map<String, GenomeAssembly> assemblyMap = new HashMap<>();

    public Map<String, GenomeAssembly> getAssemblyMap() {
        return assemblyMap;
    }

}
