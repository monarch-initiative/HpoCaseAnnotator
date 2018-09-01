package org.monarchinitiative.hpo_case_annotator.refgenome;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test serialization and deserialization of {@link GenomeAssemblies}.
 */
public class GenomeAssembliesSerializerTest {

    private static GenomeAssemblies fakeUpGenomeAssemblies() {
        GenomeAssemblies assemblies = new GenomeAssemblies();

        GenomeAssembly hg19 = GenomeAssembly.HG19;
        hg19.setFastaPath(new File("/path/to/hg19"));
        assemblies.getAssemblyMap().put(hg19.toString(), hg19);

        GenomeAssembly hg38 = GenomeAssembly.HG38;
        hg38.setFastaPath(new File("/path/to/hg38"));
        assemblies.getAssemblyMap().put(hg38.toString(), hg38);

        return assemblies;
    }


    private static Properties fakeUpProperties() {
        Properties properties = new Properties();
        properties.put("assembly.in.usage", "hg19");
        properties.put("hg19.fasta.path", "/path/to/hg19");
        properties.put("hg38.fasta.path", "/path/to/hg38");

        return properties;
    }


    @Test
    public void deserialize() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fakeUpProperties().store(os, "Test");
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        GenomeAssemblies assemblies = GenomeAssembliesSerializer.deserialize(is);

        assertThat(assemblies.getAssemblyMap().size(), is(2));
        assertThat(assemblies.getAssemblyMap().get("hg19").getFastaPath().getAbsolutePath(), is("/path/to/hg19"));
        assertThat(assemblies.getAssemblyMap().get("hg38").getFastaPath().getAbsolutePath(), is("/path/to/hg38"));
    }


    @Test
    public void serialize() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GenomeAssembliesSerializer.serialize(fakeUpGenomeAssemblies(), os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Properties properties = new Properties();
        properties.load(is);

        assertTrue(properties.containsKey("hg38.fasta.path"));
        assertTrue(properties.containsKey("hg19.fasta.path"));

        assertThat(properties.getProperty("hg19.fasta.path"), is("/path/to/hg19"));
        assertThat(properties.getProperty("hg38.fasta.path"), is("/path/to/hg38"));
    }
}