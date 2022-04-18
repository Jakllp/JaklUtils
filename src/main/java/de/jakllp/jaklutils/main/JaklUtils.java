package de.jakllp.jaklutils.main;

import de.jakllp.jaklutils.commands.*;
import de.jakllp.jaklutils.helpers.PersistencyHelper;
import de.jakllp.jaklutils.listeners.LeashListener;
import de.jakllp.jaklutils.logging.Colors;
import de.jakllp.jaklutils.logging.CustomLogger;
import de.jakllp.jaklutils.reflection.ReflectionUtil;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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
