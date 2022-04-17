package de.jakllp.jaklutils.listeners;

import de.jakllp.jaklutils.helpers.customdatatypes.StatValue;
import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class LeashListener implements Listener {
    private JaklUtils plugin;

    public LeashListener(JaklUtils plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    void leashFenceListener(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null && event.getPlayer().hasMetadata("leashingOn")) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            //Is this really a Fence
            if(block.getType().name().toUpperCase().contains("FENCE") && player.getInventory().getItemInMainHand().getType() == Material.LEAD
                    && !block.hasMetadata("inLeashing")) {
                //If first leash-point can be created
                if(LeashController.createFirstPoint(player, block) && LeashController.createHitch(block,true)) {
                    block.setMetadata("inLeashing", new StatValue(null, this.plugin));
                    if(!JaklUtils.isSilent(player)) {
                        player.sendMessage(JaklUtils.colors.getSuccess()+"First point made! Click on another fence or do /makeLeash");
                    }
                } else if(player.hasMetadata("inLeashing")
                        && LeashController.createSecondPoint(player, block) && LeashController.createHitch(block, true)) {
                    if(!JaklUtils.isSilent(player)) {
                        player.sendMessage(JaklUtils.colors.getSuccess()+"Leash created!");
                    }
                } else {
                    player.sendMessage(JaklUtils.colors.getError()+"Something went wrong!");
                }
            }
        }
    }

    //Handle disconnecting/kicked players
    @EventHandler
    void onPlayerDisconnect(PlayerQuitEvent event) {
        handlePlayerLeave(event.getPlayer());
    }
    @EventHandler
    void onPlayerDisconnect(PlayerKickEvent event) {
        handlePlayerLeave(event.getPlayer());
    }
    private void handlePlayerLeave(Player player) {
        if(player.hasMetadata("inLeashing")) {
            LeashController.abortLeashing(player);
        }
    }

    @EventHandler
    void onLeashBreak(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Bat && (entity.hasMetadata("leLeash") || entity.hasMetadata("leFirstBat"))) {
            LeashController.removeLeash((Bat) entity);
            return;
        }
        if(entity instanceof LeashHitch && (entity.hasMetadata("jaklHitch"))) {
            LeashController.removeLeash((LeashHitch) entity);
            return;
        }
    }
    @EventHandler
    void onLeashBreakButDead(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Bat && (entity.hasMetadata("leLeash") || entity.hasMetadata("leFirstBat"))) {
            LeashController.removeLeash((Bat) entity);
            return;
        }
        if(entity instanceof LeashHitch && (entity.hasMetadata("jaklHitch"))) {
            LeashController.removeLeash((LeashHitch) entity);
            return;
        }
    }
    //TODO: Add listener for frickin Leash-Hitch-Breaking
}
