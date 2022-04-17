package de.jakllp.jaklutils.commands;

import de.jakllp.jaklutils.helpers.StatValue;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandToggleLeash implements CommandTabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("JaklUtils.leashCreator") || player.isOp()) {
                if(player.hasMetadata("leashingOn")) {
                    player.removeMetadata("leashingOn", JaklUtils.plugin);
                    player.sendMessage(JaklUtils.colors.getInfo()+"You are no longer a Leash-Creator!");
                    return true;
                }
                player.setMetadata("leashingOn", new StatValue(null, JaklUtils.plugin));
                player.sendMessage(JaklUtils.colors.getInfo()+"You are now a Leash-Creator!");
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
