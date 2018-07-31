package org.monarchinitiative.hpo_case_annotator.cli;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

@Ignore("Not yet implemented") // TODO - implement test methods & converter functionality
public class Xml2JsonModelConverterTest {

    private static final File SOURCE_DIR = new File("target/test-classes/cli/input");

    private static final File DEST_DIR = new File("target/test-classes/cli/output");

    private Xml2JsonModelConverter converter;


    @Before
    public void setUp() throws Exception {
        converter = new Xml2JsonModelConverter(SOURCE_DIR, DEST_DIR);
    }


    @Test
    public void testInitialization() throws Exception {
        assertEquals("target/test-classes/cli/input", converter.getSourceDir().getPath());
        assertEquals("target/test-classes/cli/output", converter.getJsonModelParser().getModelDir().getPath());
//        assertEquals(3, converter.getJsonModelParser().getModelNames().size()); // not yet implemented
    }


    @Test
    public void testRun() throws Exception {
        converter.run();
    }
}