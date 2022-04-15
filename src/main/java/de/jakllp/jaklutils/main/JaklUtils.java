package de.jakllp.jaklutils.main;

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
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class JaklUtils extends JavaPlugin {
    public static JaklUtils plugin;
    public static LeashListener leashListener = null;
    public static CustomLogger logger = null;
    public static Colors colors = null;
    public static PersistencyHelper persistent = null;

    @Override
    public void onEnable() {
        plugin = this;
        leashListener = new LeashListener(this);
        persistent = new PersistencyHelper();
        logger = new CustomLogger(getServer().getConsoleSender(), getLogger());
        colors = new Colors("&c",
                "&7",
                "&6",
                "&4",
                "&1");

        //ToDo: Respawn leashknots
        //logger.info("Respawned all Leashknots");

        logger.info("JaklUtils loaded!");
    }

    @Override
    public void onDisable() {
        persistent.savePersistent();
        //ToDo: Save Leashknots
    }
}
