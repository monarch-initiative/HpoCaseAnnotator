package org.monarchinitiative.hpo_case_annotator.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.AbstractNamedCommand;
import org.monarchinitiative.hpo_case_annotator.cli.cmd.CommandFactory;

/**
 *
 */
public class Main {

    private final CommandFactory commandFactory;

    public Main() {
        commandFactory = new CommandFactory();
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        AbstractNamedCommand command;
        try {
            command = main.parseCli(args);
        } catch (ParameterException e) {
            e.printStackTrace();
            e.usage();
            return;
        }

        command.run();
    }

    public AbstractNamedCommand parseCli(String[] args) throws ParameterException {
        JCommander.Builder jcBuilder = JCommander.newBuilder();

        for (AbstractNamedCommand cmd : commandFactory.getCommands()) {
            jcBuilder.addCommand(cmd.getCommandName(), cmd);
        }
        JCommander jc = jcBuilder.build();
        jc.parse(args);

        // run the selected command
        String cmd = jc.getParsedCommand();
        if (cmd == null) {
            ParameterException pe = new ParameterException("No command was selected");
            pe.setJCommander(jc);
            throw pe;
        }
        return commandFactory.getCommand(cmd);

    }

}
