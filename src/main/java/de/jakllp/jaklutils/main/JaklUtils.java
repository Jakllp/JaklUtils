package de.jakllp.jaklutils.main;

import de.jakllp.jaklutils.commands.*;
import de.jakllp.jaklutils.helpers.PersistencyHelper;
import de.jakllp.jaklutils.listeners.LeashListener;
import de.jakllp.jaklutils.logging.Colors;
import de.jakllp.jaklutils.logging.CustomLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JaklUtils extends JavaPlugin {
    public static JaklUtils plugin;
    public static LeashListener leashListener = null;
    public static CustomLogger logger = null;
    public static Colors colors = null;
    public static PersistencyHelper persistent = null;
    public static List<UUID> silent = new ArrayList();

    @Override
    public void onEnable() {
        plugin = this;
        logger = new CustomLogger(getServer().getConsoleSender(), getLogger());
        colors = new Colors("&c",
                "&7",
                "&6",
                "&4",
                "&1");

        leashListener = new LeashListener(this);
        persistent = new PersistencyHelper(plugin);
        persistent.restorePersistent();

        // register commands
        //getCommand("JaklUtils").setExecutor(new CommandHelp());
        getCommand("abortleash").setExecutor(new CommandAbortLeash());
        getCommand("jaklsilent").setExecutor(new CommandSilenceMe());
        getCommand("makeleash").setExecutor(new CommandMakeLeash());
        getCommand("toggleleash").setExecutor(new CommandToggleLeash());
        getCommand("toggleknot").setExecutor(new CommandToggleKnotting());

        //bStats
        int pluginId = 14990;
        Metrics metrics = new Metrics(this, pluginId);

        logger.info("JaklUtils loaded!");
    }

    @Override
    public void onDisable() {
        persistent.savePersistent();
    }

    public static boolean isSilent(Player player) {
        if (silent.contains(player.getUniqueId())) {
            return true;
        }
        return false;
    }
}
