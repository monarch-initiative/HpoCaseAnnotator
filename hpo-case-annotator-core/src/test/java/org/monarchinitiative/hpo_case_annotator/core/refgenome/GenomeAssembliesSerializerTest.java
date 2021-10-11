package org.monarchinitiative.hpo_case_annotator.core.refgenome;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test serialization and deserialization of {@link GenomeAssemblies}.
 */
public class GenomeAssembliesSerializerTest {

    private static GenomeAssemblies fakeUpGenomeAssemblies() {
        GenomeAssemblies assemblies = new GenomeAssemblies();
        assemblies.putAssembly(GenomeAssembly.GRCH_38, Paths.get("/path/to/grch38.fa")); // hg38
        assemblies.putAssembly(GenomeAssembly.GRCH_37, Paths.get("/path/to/grch37.fa")); // hg19
        return assemblies;
    }


    private static Properties fakeUpProperties() {
        Properties properties = new Properties();
        properties.put("GRCH_38.fasta.path", "/path/to/grch38.fa");
        properties.put("GRCH_37.fasta.path", "/path/to/grch37.fa");

        return properties;
    }


    @Test
    public void deserialize() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fakeUpProperties().store(os, "Test");
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        GenomeAssemblies assemblies = GenomeAssembliesSerializer.deserialize(is);

        assertThat(assemblies.getAssemblyMap().size(), is(2));
        assertThat(assemblies.getAssemblyMap().get(GenomeAssembly.GRCH_38).toFile().getName(), is("grch38.fa"));
        assertThat(assemblies.getAssemblyMap().get(GenomeAssembly.HG_19).toFile().getName(), is("grch37.fa"));
    }


    @Test
    public void serialize() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GenomeAssembliesSerializer.serialize(fakeUpGenomeAssemblies(), os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Properties properties = new Properties();
        properties.load(is);

        assertTrue(properties.containsKey("GRCH_38.fasta.path"));
        assertThat(properties.getProperty("GRCH_38.fasta.path"), is("/path/to/grch38.fa"));

        assertTrue(properties.containsKey("GRCH_37.fasta.path"));
        assertThat(properties.getProperty("GRCH_37.fasta.path"), is("/path/to/grch37.fa"));
    }
}