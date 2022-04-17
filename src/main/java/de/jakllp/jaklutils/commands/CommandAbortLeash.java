package de.jakllp.jaklutils.commands;

import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandAbortLeash implements CommandTabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("JaklUtils.leashCreator") || player.isOp()) {
                if(player.hasMetadata("inLeashing")) {
                    LeashController.abortLeashing(player);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
