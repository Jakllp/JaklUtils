package de.jakllp.jaklutils.listeners;

import de.jakllp.jaklutils.helpers.StatValue;
import de.jakllp.jaklutils.main.JaklUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
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
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    void leashFenceListener(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if(block.getType().name().toUpperCase().contains("FENCE") && player.getInventory().getItemInMainHand().getType() == Material.LEAD
                    && !block.hasMetadata("hasLeash")) {
                if(player.isEmpty()) {
                    boolean needsHitch = true;
                    for(Entity entity: block.getWorld().getNearbyEntities(block.getLocation(),1,1,1)) {
                        if(entity instanceof LeashHitch) {
                            needsHitch=false;
                        }
                    }

                    if(needsHitch) {
                        LeashHitch leHitch = (LeashHitch) block.getWorld().spawnEntity(block.getLocation(), EntityType.LEASH_HITCH);
                        this.plugin.persistent.addKnot(leHitch.getLocation());
                        leHitch.setMetadata("jaklLeash",new StatValue(null, this.plugin));
                    }

                    Bat bat1 = (Bat) player.getWorld().spawnEntity(block.getBoundingBox().getCenter().toLocation(block.getWorld()).subtract(0,0,0.1875), EntityType.BAT);
                    bat1.setSilent(true);
                    bat1.setAI(false);
                    bat1.setInvulnerable(true);
                    //bat1.setInvisible(true);
                    bat1.setPersistent(false);
                    Bat bat2 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
                    bat2.setSilent(true);
                    bat2.setAI(false);
                    bat2.setInvulnerable(true);
                    //bat2.setInvisible(true);
                    bat2.setPersistent(false);
                    player.addPassenger(bat2);
                    bat1.setLeashHolder(bat2);
                    bat2.setMetadata("leLeash",new StatValue(bat1,this.plugin));

                    block.setMetadata("hasLeash", new StatValue(null, this.plugin));
                } else if(player.getPassengers().get(0).hasMetadata("leLeash") || player.getPassengers().get(0) instanceof Bat) {
                    boolean needsHitch = true;
                    for(Entity entity: block.getWorld().getNearbyEntities(block.getLocation(),1,1,1)) {
                        if(entity instanceof LeashHitch) {
                            needsHitch=false;
                        }
                    }
                    if(needsHitch) {
                        LeashHitch leHitch = (LeashHitch) block.getWorld().spawnEntity(block.getLocation(), EntityType.LEASH_HITCH);
                        this.plugin.persistent.addKnot(leHitch.getLocation());
                        leHitch.setMetadata("jaklLeash",new StatValue(null, this.plugin));
                    }

                    //Move the bat
                    Bat leBat = (Bat) player.getPassengers().get(0);
                    player.eject();
                    leBat.teleport(block.getBoundingBox().getCenter().toLocation(block.getWorld()));

                    //Get other bat
                    Bat leFirstBat = (Bat) ((StatValue)leBat.getMetadata("leLeash").get(0)).getValue();
                    //Make both persistent
                    leBat.setPersistent(true);
                    leFirstBat.setPersistent(true);

                    //Remove metadata of first block
                    leFirstBat.getLocation().getBlock().removeMetadata("hasLeash",this.plugin);
                }
            }
        }
    }

    //TODO: Need listeners for LeashBreak and such
}
