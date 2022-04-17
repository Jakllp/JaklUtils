package de.jakllp.jaklutils.commands;

import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandSilenceMe implements CommandTabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("JaklUtils.canSilent") || player.isOp()) {
                if(JaklUtils.isSilent(player)) {
                    JaklUtils.silent.remove(player.getUniqueId());
                    player.sendMessage(JaklUtils.colors.getSuccess()+"You will get all messages again!");
                    return true;
                }
                JaklUtils.silent.add(player.getUniqueId());
                player.sendMessage(JaklUtils.colors.getSuccess()+"You won't get (unimportant) messages anymore!");
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
