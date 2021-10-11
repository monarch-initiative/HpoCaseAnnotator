package org.monarchinitiative.hpo_case_annotator.model.proto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.test_resources.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.utils.ModelUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelUtilsTest {

    private Publication publication;

    private DiseaseCase diseaseCase;


    @BeforeEach
    public void setUp() throws Exception {
        diseaseCase = TestResources.benMahmoud2013B3GLCT();
        publication = diseaseCase.getPublication();
    }


    @Test
    public void getNameFor() {
        assertThat(ModelUtils.getFileNameFor(diseaseCase), is("Ben_Mahmoud-2013-B3GLCT"));
    }


    @Test
    public void getFileNameWithSampleId() {
        assertThat(ModelUtils.getFileNameWithSampleId(diseaseCase), is("Ben_Mahmoud-2013-B3GLCT-Tunisian_patients"));
    }

    @Test
    public void getFirstAuthorsSurname() {
        assertThat(ModelUtils.getFirstAuthorsSurname(publication.getAuthorList()), is("Ben_Mahmoud"));
    }

    @Test
    public void getPublicationSummary() {
        String summary = ModelUtils.getPublicationSummary(publication);
        String expected = "Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E, Belguith N," +
                " Fakhfakh F. First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo " +
                "approach in Tunisian patients with typical Peters plus syndrome. Gene. 2013 532(1):13-7. PMID:23954224";
        assertThat(summary, equalTo(expected));
    }

    @Test
    public void firstAuthorDoesNotHaveFirstNameInitial() {
        Publication publication = Publication.newBuilder()
                .setAuthorList("Irfanullah, Umair M, Khan S, Ahmad W")
                .build();
        String surname = ModelUtils.getFirstAuthorsSurname(publication.getAuthorList());
        assertThat(surname, is(equalTo("Irfanullah")));
    }

    @Test
    public void nonAsciiCharactersAreReplacedWithAsciiWithinFileName() {
        List<String> payload = Arrays.asList("Treviño-Alanís JC", "Anczuków O", "Béroud C", "Brüggenwirth HT", "Schön H", "Gonçalves V");
        List<String> expected = Arrays.asList("Trevino-Alanis", "Anczukow", "Beroud", "Bruggenwirth", "Schon", "Goncalves");

        for (int i = 0; i < payload.size(); i++) {
            DiseaseCase dc = DiseaseCase.newBuilder()
                    .setPublication(Publication.newBuilder().setAuthorList(payload.get(i)).setYear("2000").build())
                    .setGene(Gene.newBuilder().setEntrezId(1234).setSymbol("ABCD4").build())
                    .setFamilyInfo(FamilyInfo.newBuilder().setAge("P12Y").setFamilyOrProbandId("proband1").setSex(Sex.MALE).build())
                    .build();
            assertThat(ModelUtils.getFileNameWithSampleId(dc),
                    is(expected.get(i) + "-2000-ABCD4-proband1"));
        }
    }

    @Test
    public void normalizeAsciiText() {
        List<String> payloads = Arrays.asList("Cuchet-Lourenço", "Treviño-Alanís JC", "Anczuków O", "Béroud C", "Brüggenwirth HT", "Schön H", "Gonçalves V", "Flønes K");
        List<String> actual = payloads.stream()
                .map(ModelUtils::normalizeAsciiText)
                .collect(Collectors.toList());
        assertThat(actual, hasItems("Cuchet-Lourenco", "Trevino-Alanis JC", "Anczukow O", "Beroud C", "Bruggenwirth HT", "Schon H", "Goncalves V", "Flønes K"));

    }
}