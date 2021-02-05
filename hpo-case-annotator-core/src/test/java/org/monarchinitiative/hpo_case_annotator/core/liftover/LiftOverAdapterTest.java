package org.monarchinitiative.hpo_case_annotator.core.liftover;

import htsjdk.samtools.util.Interval;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LiftOverAdapterTest {

    private static final Path CHAIN_DIR = Paths.get(LiftOverAdapterTest.class.getResource("").getPath());

    private LiftOverAdapter instance;

    @Before
    public void setUp() throws Exception {
        instance = LiftOverAdapter.ofChainFolder(CHAIN_DIR.toFile());
    }


    @Test
    public void liftGrch37ToGrch38_SURF1() {
        Interval interval = new Interval("chr9", 136_223_175, 136_223_180);
        Optional<Interval> liftedOptional = instance.liftOver(interval, GenomeAssembly.HG_19, GenomeAssembly.HG_38);

        assertThat(liftedOptional.isPresent(), is(true));

        Interval lifted = liftedOptional.get();
        assertThat(lifted.getContig(), is("chr9"));
        assertThat(lifted.getStart(), is(133_356_320));
        assertThat(lifted.getEnd(), is(133_356_325));
    }

    @Test
    public void failDueToMissingChain() {
        Interval anyInterval = new Interval("chr1", 100, 200);

        Optional<Interval> liftedOptional = instance.liftOver(anyInterval, GenomeAssembly.GRCH_38, GenomeAssembly.HG_18);
        assertThat(liftedOptional.isPresent(), is(false));
    }
}