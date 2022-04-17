package de.jakllp.jaklutils.commands;

import de.jakllp.jaklutils.helpers.StatValue;
import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandMakeLeash implements CommandTabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("JaklUtils.leashCreator") || player.isOp()) {
                boolean worked = false;
                if(player.hasMetadata("inLeashing")) {
                    worked = LeashController.createSecondPoint(player);
                    player.sendMessage(JaklUtils.colors.getSuccess()+"Leash created!");
                } else {
                    worked = LeashController.createFirstPoint(player);
                    player.sendMessage(JaklUtils.colors.getSuccess()+"First Point created! Click on a fence or do /makeLeash");
                }

                if(!worked) {
                    player.sendMessage(JaklUtils.colors.getError()+"Something went wrong!");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
