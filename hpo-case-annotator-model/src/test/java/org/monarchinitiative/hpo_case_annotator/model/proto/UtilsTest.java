package org.monarchinitiative.hpo_case_annotator.model.proto;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UtilsTest {

    private Publication publication;

    private DiseaseCase diseaseCase;


    @Before
    public void setUp() throws Exception {
        diseaseCase = ModelsForTesting.benMahmoud2013B3GLCT();
        publication = diseaseCase.getPublication();
    }


    @Test
    public void getNameFor() {
        assertThat(Utils.getNameFor(diseaseCase), is("Ben_Mahmoud-2013-B3GLCT"));
    }


    @Test
    public void getFirstAuthorsSurname() {
        assertThat(Utils.getFirstAuthorsSurname(publication), is("Ben_Mahmoud"));
    }
}