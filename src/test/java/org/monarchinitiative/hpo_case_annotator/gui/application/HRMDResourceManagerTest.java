package org.monarchinitiative.hpo_case_annotator.gui.application;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class HRMDResourceManagerTest {

    private HRMDResourceManager emptyManager, initializedManager;


    @Before
    public void setUp() throws Exception {
        initializedManager = new HRMDResourceManager(new File("target/test-classes/test-hrmd-resources.json"));
        emptyManager = new HRMDResourceManager(null); // either default location
        emptyManager.getResources().setDataDir("");
        emptyManager.getResources().setRefGenomeDir("");
        emptyManager.getResources().setHpOBOPath("");
        emptyManager.getResources().setEntrezGenePath("");
        emptyManager.getResources().setDiseaseCaseDir("");
        emptyManager.getResources().setBiocuratorId("");
    }


    @Test
    @Ignore("TODO - this doesn't work. Solve how to make sure that required files like HP.obo will be available for " +
            "testing.")
    public void isInitialized() throws Exception {
        assertTrue(initializedManager.isInitialized());
        assertFalse(emptyManager.isInitialized());
    }


    @Test
    public void getResources() throws Exception {
        HRMDResources initialized = initializedManager.getResources();
        assertEquals("target/test-classes", initialized.getDataDir());
        assertEquals("target/test-classes", initialized.getDiseaseCaseDir());
        assertEquals("target/test-classes/HP.obo", initialized.getHpOBOPath());
        assertEquals("target/test-classes/Homo_sapiens.gene_info.gz", initialized.getEntrezGenePath());
        assertEquals("target/test-classes", initialized.getRefGenomeDir());

        HRMDResources empty = emptyManager.getResources();
        assertEquals("", empty.getDataDir());
        assertEquals("", empty.getDiseaseCaseDir());
        assertEquals("", empty.getHpOBOPath());
        assertEquals("", empty.getEntrezGenePath());
        assertEquals("", empty.getRefGenomeDir());
    }


    @Test
    public void getDataDir() throws Exception {
        assertFalse(emptyManager.getDataDir().isPresent());
        assertTrue(initializedManager.getDataDir().isPresent());
        assertEquals("target/test-classes", initializedManager.getDataDir().get().getPath());
    }

}