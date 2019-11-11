package org.monarchinitiative.hpo_case_annotator.core.liftover;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.hpo_case_annotator.model.proto.VariantPosition;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VariantPositionLiftOverTest {

    private VariantPositionLiftOver instance;

    @Before
    public void setUp() throws Exception {
        // empty string is the directory
        instance = new VariantPositionLiftOver(new File(getClass().getResource("").getFile()));
    }


    /**
     * Test that the variant <code>chr9:136223175C>A</code> is converted to <code>chr9:133356320C>A</code>.
     * <p>
     * The variant in question affects the first nucleotide of the 2nd exon of transcript <code>NM_003172</code> of the
     * <em>SURF1</em> gene.
     */
    @Test
    public void liftGrch37ToGrch38_SURF1() {
        VariantPosition surf1Pos = VariantPosition.newBuilder()
                .setGenomeAssembly(GenomeAssembly.HG_19)
                .setContig("chr9")
                .setPos(136223175)
                .setRefAllele("C")
                .setAltAllele("A")
                .build();

        final Optional<VariantPosition> liftedOptional = instance.liftOver(surf1Pos, GenomeAssembly.HG_19, GenomeAssembly.HG_38);
        assertThat(liftedOptional.isPresent(), is(true));

        VariantPosition lifted = liftedOptional.get();
        assertThat(lifted.getGenomeAssembly(), is(GenomeAssembly.HG_38));
        assertThat(lifted.getContig(), is("chr9"));
        assertThat(lifted.getPos(), is(133356320));
        assertThat(lifted.getRefAllele(), is("C"));
        assertThat(lifted.getAltAllele(), is("A"));
    }

    /**
     * Test that the variant <code>chr13:20763742T>C</code> is converted to <code>chr13:20189603T>C</code>.
     * <p>
     * The variant in question affects the first nucleotide of the 2nd exon of transcript <code>NM_004004</code> of the
     * <em>GJB2</em> gene.
     */
    @Test
    public void liftHg19ToHg38_GJB2() {
        VariantPosition gjb2 = VariantPosition.newBuilder()
                .setGenomeAssembly(GenomeAssembly.HG_19)
                .setContig("chr13")
                .setPos(20763742)
                .setRefAllele("T")
                .setAltAllele("C")
                .build();

        final Optional<VariantPosition> liftedOptional = instance.liftOver(gjb2, GenomeAssembly.HG_19, GenomeAssembly.HG_38);
        assertThat(liftedOptional.isPresent(), is(true));

        VariantPosition lifted = liftedOptional.get();
        assertThat(lifted.getGenomeAssembly(), is(GenomeAssembly.HG_38));
        assertThat(lifted.getContig(), is("chr13"));
        assertThat(lifted.getPos(), is(20189603));
        assertThat(lifted.getRefAllele(), is("T"));
        assertThat(lifted.getAltAllele(), is("C"));
    }

    @Test
    public void failDueToMissingChain() {
        VariantPosition variant = VariantPosition.newBuilder().setGenomeAssembly(GenomeAssembly.GRCH_38).build();

        Optional<VariantPosition> liftedOptional = instance.liftOver(variant, GenomeAssembly.GRCH_38, GenomeAssembly.HG_18);
        assertThat(liftedOptional.isPresent(), is(false));
    }

    @Test
    public void failDueToIncoherentGenomeAssembly() {
        VariantPosition variant = VariantPosition.newBuilder()
                .setGenomeAssembly(GenomeAssembly.HG_18)
                .build();

        Optional<VariantPosition> liftedOptional = instance.liftOver(variant, GenomeAssembly.HG_19, GenomeAssembly.GRCH_38);
        assertThat(liftedOptional.isPresent(), is(false));
    }
}