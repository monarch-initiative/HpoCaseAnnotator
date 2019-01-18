package org.monarchinitiative.hpo_case_annotator.model.proto;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModelExample;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModelUtilsTest {

    private Publication publication;

    private DiseaseCase diseaseCase;


    @Before
    public void setUp() throws Exception {
        diseaseCase = TestResources.benMahmoud2013B3GLCT();
        publication = diseaseCase.getPublication();
    }


    @Test
    public void getNameFor() {
        assertThat(ModelUtils.getNameFor(diseaseCase), is("Ben_Mahmoud-2013-B3GLCT"));
    }


    @Test
    public void getFirstAuthorsSurname() {
        assertThat(ModelUtils.getFirstAuthorsSurname(publication), is("Ben_Mahmoud"));
    }

    @Test
    public void getPublicationSummary() {
        String summary = ModelUtils.getPublicationSummary(publication);
        String expected = "Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E, Belguith N," +
                " Fakhfakh F. First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo " +
                "approach in Tunisian patients with typical Peters plus syndrome. Gene. 2013 532(1):13-7. PMID:23954224";
        assertThat(summary, equalTo(expected));
    }
}