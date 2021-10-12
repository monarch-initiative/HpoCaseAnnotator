package org.monarchinitiative.hpo_case_annotator.cli.cmd;

import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class knows about all the commands available in the `hpo-case-annotator-cli` module.
 */
public class CommandFactory {

    private final Map<String, AbstractNamedCommand> commandMap;

    public CommandFactory() {
        this.commandMap = new HashMap<>();

        ConvertCommand convert = new ConvertCommand();
        this.commandMap.put(convert.getCommandName(), convert);
    }

    public Set<AbstractNamedCommand> getCommands() {
        return ImmutableSet.copyOf(commandMap.values());
    }

    public AbstractNamedCommand getCommand(String commandName) {
        return commandMap.get(commandName);
    }
}
