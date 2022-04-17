package de.jakllp.jaklutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface CommandTabCompleter extends CommandExecutor, TabCompleter {
    default List<String> filterTabCompletionResults(Collection<String> collection, String startsWith) {
        return collection.stream().filter(s -> s.toLowerCase().startsWith(startsWith.toLowerCase())).sorted().collect(Collectors.toList());
    }
}
