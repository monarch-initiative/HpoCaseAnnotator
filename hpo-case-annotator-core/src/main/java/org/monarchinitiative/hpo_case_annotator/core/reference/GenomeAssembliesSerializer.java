package org.monarchinitiative.hpo_case_annotator.core.reference;

import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Serialize and deserialize data describing {@link GenomeAssembly} and return as {@link GenomeAssemblies} object.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class GenomeAssembliesSerializer {

    private GenomeAssembliesSerializer() {
        // private no-op
    }

    /**
     * @param is {@link InputStream} with data
     * @return {@link GenomeAssemblies} populated with content of the <code>is</code>
     * @throws IOException in case of I/O error
     */
    public static GenomeAssemblies deserialize(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);

        GenomeAssemblies assemblies = new GenomeAssemblies();

        for (GenomeAssembly assembly : GenomeAssembly.values()) {
            String key = keyFor(assembly);
            String val = properties.getProperty(key);
            if (val != null) {
                final Path fastaPath = Paths.get(val);
                assemblies.putAssembly(assembly, fastaPath);
            }
        }
        return assemblies;
    }


    /**
     * @param assemblies {@link GenomeAssemblies} to be persisted
     * @param os         {@link OutputStream} to be used to persist the data
     * @throws IOException in case of I/O error
     */
    public static void serialize(GenomeAssemblies assemblies, OutputStream os) throws IOException {
        Properties properties = new Properties();

        // all defined assemblies
        assemblies.getAssemblyMap()
                .forEach((ga, path) -> properties.put(keyFor(ga), path.toFile().getAbsolutePath()));

        properties.store(os, "Genome assemblies definition file for Hpo Case Annotator");
    }


    private static String keyFor(GenomeAssembly assembly) {
        return assembly.toString() + ".fasta.path";
    }
}
