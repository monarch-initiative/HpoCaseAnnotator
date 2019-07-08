package org.monarchinitiative.hpo_case_annotator.cli;

import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.AbstractNamedCommand;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.StatsCommand;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MainTest {

    private Main main;

    @Before
    public void setUp() throws Exception {
        main = new Main();
    }

    @Test(expected = ParameterException.class)
    public void missingRequiredArgs() {
        String[] args = {"stats"};
        main.parseCli(args);
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