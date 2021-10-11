package org.monarchinitiative.hpo_case_annotator.cli;

import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.AbstractNamedCommand;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.StatsCommand;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {

    private Main main;

    @BeforeEach
    public void setUp() throws Exception {
        main = new Main();
    }

    @Test
    public void missingRequiredArgs() {
        String[] args = {"stats"};
        assertThrows(ParameterException.class, () -> main.parseCli(args));
    }

    @Test
    public void properInitializationWorks() {
        String[] args = {"stats", "-o", "/path/to/output", "-m", "/path/to/model/dir"};
        AbstractNamedCommand acmd = main.parseCli(args);
        assertThat(acmd, is(notNullValue()));
        assertThat(acmd, is(instanceOf(StatsCommand.class)));

        StatsCommand cmd = ((StatsCommand) acmd);
        assertThat(cmd.getOutputPathString(), is("/path/to/output"));
        assertThat(cmd.getModelDirectoryPath(), is("/path/to/model/dir"));
    }
}