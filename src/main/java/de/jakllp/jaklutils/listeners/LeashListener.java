package de.jakllp.jaklutils.listeners;

import de.jakllp.jaklutils.entities.AbstractLeashEntity;
import de.jakllp.jaklutils.entities.LeashArmorStand;
import de.jakllp.jaklutils.main.JaklUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.CraftEffect;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Constructor;

public class LeashListener implements Listener {
    private JaklUtils plugin;

    public LeashListener(JaklUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void leashFenceListener(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if(block.getType().name().toUpperCase().contains("FENCE")) {
                Bukkit.getConsoleSender().sendMessage("Fence");
                LeashHitch leHitch = (LeashHitch) block.getWorld().spawnEntity(block.getLocation(), EntityType.LEASH_HITCH);
                event.getPlayer().setLeashHolder(leHitch);
            }
        }
    }

    @EventHandler
    void leashEntityListener(PlayerInteractAtEntityEvent event) {
        Bukkit.getConsoleSender().sendMessage("GoingIn");
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if(player.getInventory().getItemInMainHand().getType() == Material.LEAD) {
            Bukkit.getConsoleSender().sendMessage("HasLead");
            if(entity instanceof LivingEntity) {

                LivingEntity lEntity = (LivingEntity) entity;
                Bukkit.getConsoleSender().sendMessage("Should leash");

                //Create Minecraft Entity... Somehow
                ServerLevel leWorld = ((CraftWorld)player.getWorld()).getHandle();

                AbstractLeashEntity leMinecraftEntity = new LeashArmorStand(leWorld);

                leMinecraftEntity.setLocation(player.getLocation());

                leWorld.addFreshEntity(leMinecraftEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

                leMinecraftEntity.setLeashedTo(((CraftEntity)lEntity).getHandle(),true);
            }
        }
    }
}
