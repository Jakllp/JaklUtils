package de.jakllp.jaklutils.listeners;

import de.jakllp.jaklutils.helpers.customdatatypes.StatValue;
import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if(block != null && player.hasMetadata("leashingOn") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Is this really a Fence
            if(block.getType().name().toUpperCase().contains("FENCE") && player.getInventory().getItemInMainHand().getType() == Material.LEAD
                    && !block.hasMetadata("inLeashing")) {
                //If first leash-point can be created
                if(LeashController.createFirstPoint(player, block) && LeashController.createHitch(block)) {
                    block.setMetadata("inLeashing", new StatValue(null, this.plugin));
                    if(!JaklUtils.isSilent(player)) {
                        player.sendMessage(JaklUtils.colors.getSuccess()+"First point made! Click on another fence or do /makeLeash");
                    }
                } else if(player.hasMetadata("inLeashing")
                        && LeashController.createSecondPoint(player, block) && LeashController.createHitch(block)) {
                    if(!JaklUtils.isSilent(player)) {
                        player.sendMessage(JaklUtils.colors.getSuccess()+"Leash created!");
                    }
                } else {
                    player.sendMessage(JaklUtils.colors.getError()+"Something went wrong!");
                }
            }
        }
        if(block != null && player.hasMetadata("knottingOn") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Is this really a Fence
            if (block.getType().name().toUpperCase().contains("FENCE") && player.getInventory().getItemInMainHand().getType() == Material.LEAD
                    && !block.hasMetadata("inLeashing")) {
                if(LeashController.createHitch(block)) {
                    if(!JaklUtils.isSilent(player)) {
                        player.sendMessage(JaklUtils.colors.getSuccess()+"Knot created");
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
        if(event.getDamager() instanceof Player && (event.getDamager().isOp() || event.getDamager().hasPermission("JaklUtils.leashCreator")))
            handleLeashBreak(event.getEntity());
    }
    private void handleLeashBreak(Entity entity) {
        if(entity instanceof Bat && LeashController.isInBatMap(entity)) {
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
