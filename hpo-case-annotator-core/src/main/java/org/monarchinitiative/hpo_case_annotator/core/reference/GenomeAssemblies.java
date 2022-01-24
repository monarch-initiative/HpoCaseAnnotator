package org.monarchinitiative.hpo_case_annotator.core.reference;


import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * This class manages fasta files and takes care of {@link SequenceDao} initialization.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
@Deprecated
public class GenomeAssemblies implements AutoCloseable {

    private final List<GenomeAssembly> assemblies;

    private final List<Path> fastaPaths;

    private final Map<GenomeAssembly, SequenceDao> sequenceDaoMap;

    private final Function<Path, SequenceDao> sequenceDaoFactory;

    public GenomeAssemblies(Function<Path, SequenceDao> sequenceDaoFactory) {
        this.sequenceDaoFactory = sequenceDaoFactory;
        assemblies = new ArrayList<>();
        fastaPaths = new ArrayList<>();
        sequenceDaoMap = new HashMap<>();
    }

    public GenomeAssemblies() {
        this(p -> new SingleFastaSequenceDao(p.toFile()));
    }

    public void putAssembly(GenomeAssembly assembly, Path fastaPath) {
        assemblies.add(assembly);
        fastaPaths.add(fastaPath);
    }

    public boolean hasFastaForAssembly(GenomeAssembly assembly) {
        if (assemblies.contains(assembly)) {
            final int idx = assemblies.indexOf(assembly);
            if (idx < fastaPaths.size()) {
                final File fasta = fastaPaths.get(idx).toFile();
                return fasta.isFile();
            }
        }
        return false;
    }

    public Map<GenomeAssembly, Path> getAssemblyMap() {
        Map<GenomeAssembly, Path> builder = new HashMap<>();
        for (int i = 0; i < assemblies.size(); i++) {
            builder.put(assemblies.get(i), fastaPaths.get(i));
        }
        return Map.copyOf(builder);
    }


    public void removeAssembly(GenomeAssembly assembly) {
        if (assemblies.contains(assembly)) {
            final int idx = assemblies.indexOf(assembly);
            if (idx < fastaPaths.size()) {
                fastaPaths.remove(idx);
            }
            assemblies.remove(idx);
        }
    }

    public Optional<SequenceDao> getSequenceDaoForAssembly(GenomeAssembly assembly) {
        if (!hasFastaForAssembly(assembly)) { // we do not have the path to the genome assembly fasta file yet
            return Optional.empty();
        }

        if (!sequenceDaoMap.containsKey(assembly)) { // we do have path to the fasta file, but the fasta has not been yet opened
            sequenceDaoMap.put(assembly, sequenceDaoFactory.apply(getAssemblyMap().get(assembly)));
        }
        return Optional.ofNullable(sequenceDaoMap.get(assembly));
    }

    @Override
    public void close() throws Exception {
        for (SequenceDao dao : sequenceDaoMap.values()) {
            dao.close();
        }
    }
}
