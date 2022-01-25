package org.monarchinitiative.hpo_case_annotator.core.liftover;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LiftOverAdapterTest {

    private static final Path CHAIN_DIR = Paths.get(LiftOverAdapterTest.class.getResource("").getPath());

    private LiftOverAdapter instance;

    @BeforeEach
    public void setUp() throws Exception {
        instance = LiftOverAdapter.ofChainFolder(CHAIN_DIR.toFile());
    }


    @Test
    public void liftGrch37ToGrch38_SURF1() {
        LiftOverService.ContigPosition position = new LiftOverService.ContigPosition("chr9", 136_223_175);
        Optional<LiftOverService.ContigPosition> liftedOptional = instance.liftOver(position, "GRCh37.p13", "GRCh38.p13");

        assertThat(liftedOptional.isPresent(), is(true));

        LiftOverService.ContigPosition lifted = liftedOptional.get();
        assertThat(lifted.contig(), is("chr9"));
        assertThat(lifted.position(), is(133_356_320));
    }

    @Test
    public void failDueToMissingChain() {
        LiftOverService.ContigPosition anyPosition = new LiftOverService.ContigPosition("chr9", 136_223_175);

        Optional<LiftOverService.ContigPosition> liftedOptional = instance.liftOver(anyPosition, "GRCh38.p13", "GRCh37.p13");
        assertThat(liftedOptional.isPresent(), is(false));
    }
}