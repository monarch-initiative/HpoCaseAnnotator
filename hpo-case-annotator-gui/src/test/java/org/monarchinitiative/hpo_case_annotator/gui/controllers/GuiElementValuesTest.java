package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GuiElementValuesTest {

    private GuiElementValues instance;

    @Before
    public void setUp() throws Exception {
        try (InputStream is = GuiElementValuesTest.class.getResourceAsStream("test-gui-elements-values.yml")) {
            instance = GuiElementValues.guiElementValuesFrom(is);
        }
    }

    @Test
    public void loadingFromYaml() throws Exception {
        // instance is loaded in setUp method

        // assert
        assertThat(instance.getGenomeBuild().size(), is(3));
        assertThat(instance.getGenomeBuild(), hasItems(GenomeAssembly.GRCH_37, GenomeAssembly.GRCH_38, GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY));

        assertThat(instance.getChromosome().size(), is(4));
        assertThat(instance.getChromosome(), hasItems("1", "4", "5", "X"));

        assertThat(instance.getVariantClass().size(), is(3));
        assertThat(instance.getVariantClass(), hasItems("coding", "enhancer", "promoter"));

        assertThat(instance.getPathomechanism().size(), is(3));
        assertThat(instance.getPathomechanism(), hasItems("unknown", "coding|missense", "coding|stop-codon"));

        assertThat(instance.getConsequence().size(), is(2));
        assertThat(instance.getConsequence(), hasItems("Exon skipping", "Intron retention"));

        assertThat(instance.getDiseaseDatabases().size(), is(2));
        assertThat(instance.getDiseaseDatabases(), hasItems("OMIM", "NCI"));

        assertThat(instance.getComparability().size(), is(2));
        assertThat(instance.getComparability(), hasItems("yes", "no"));

        assertThat(instance.getCosegregation().size(), is(2));
        assertThat(instance.getCosegregation(), hasItems("yes", "no"));

        assertThat(instance.getReporter().size(), is(3));
        assertThat(instance.getReporter(), hasItems("no", "up", "down"));

        assertThat(instance.getEmsa().size(), is(2));
        assertThat(instance.getEmsa(), hasItems("yes", "no"));

        assertThat(instance.getOtherChoices().size(), is(1));
        assertThat(instance.getOtherChoices(), hasItems("no"));

        assertThat(instance.getOtherEffect().size(), is(2));
        assertThat(instance.getOtherEffect(), hasItems("Telomerase", "Nonspecific_EMSA"));
    }


    @Test
    public void toStringIsTested() {
        String expected = "GuiElementValues{genomeBuild=[UNKNOWN_GENOME_ASSEMBLY, GRCH_38, GRCH_37], chromosome=[1, 4, 5, X], " +
                "variantClass=[coding, enhancer, promoter], pathomechanism=[unknown, coding|missense, coding|stop-codon], " +
                "consequence=[Exon skipping, Intron retention], reporter=[no, up, down], " +
                "emsa=[yes, no], otherChoices=[no], otherEffect=[Telomerase, Nonspecific_EMSA], " +
                "diseaseDatabases=[OMIM, NCI], cosegregation=[yes, no], comparability=[yes, no]}";
        // act
        final String actual = instance.toString();
        // assert
        assertThat(actual, is(expected));

    }
}