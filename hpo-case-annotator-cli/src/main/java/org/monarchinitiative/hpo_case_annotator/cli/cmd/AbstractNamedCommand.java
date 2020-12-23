package org.monarchinitiative.hpo_case_annotator.cli.cmd;

/**
 *
 */
public abstract class AbstractNamedCommand {

    public abstract String getCommandName();

    public abstract void run() throws Exception;
}
