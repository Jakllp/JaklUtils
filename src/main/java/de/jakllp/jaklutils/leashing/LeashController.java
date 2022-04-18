package de.jakllp.jaklutils.leashing;

import de.jakllp.jaklutils.helpers.customdatatypes.BatContainer;
import de.jakllp.jaklutils.helpers.customdatatypes.StatValue;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class LeashController {
    private static HashMap<UUID, BatContainer> batMap = new HashMap();

    public static boolean createFirstPoint(Player player) {
        return createFirstPoint(player, player.getLocation(),false);
    }

    public static boolean createFirstPoint(Player player, Block block) {
        Location loc = block.getLocation();
        Location loc2 = new Location(loc.getWorld(), loc.getBlockX()+0.5, block.getBoundingBox().getCenter().toLocation(block.getWorld()).getY(), loc.getBlockZ()+0.3125);

        return createFirstPoint(player, loc2, true);
    }

    public static boolean createFirstPoint(Player player, Location loc, boolean onFence) {
        if(player.isEmpty()) {
            //Spawn the bats
            Bat bat1 = (Bat) player.getWorld().spawnEntity(loc, EntityType.BAT);
            Bat bat2 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
            giveBatTreatment(bat1);
            giveBatTreatment(bat2);

            bat1.setAwake(true);
            if(onFence) {
                bat1.teleport(loc.subtract(0,0.4,0));
            }

            //Link bats and such
            player.addPassenger(bat2);
            bat1.setLeashHolder(bat2);

            //MetaData for Recognition
            bat1.setMetadata("leFirstBat",new StatValue(null, JaklUtils.plugin));
            bat2.setMetadata("leLeash",new StatValue(bat1, JaklUtils.plugin));
            player.setMetadata("inLeashing",new StatValue(null, JaklUtils.plugin));
            return true;
        }

        return false;
    }
    private static void giveBatTreatment(Bat bat) {
        bat.setSilent(true);
        bat.setAI(false);
        bat.setInvulnerable(true);
        bat.setGravity(false);
        bat.setInvisible(true);
        bat.setPersistent(false);
    }

    public static boolean createSecondPoint(Player player) {
        return createSecondPoint(player, player.getLocation(), false);
    }

    public static boolean createSecondPoint(Player player, Block block) {
        Location loc = block.getLocation();
        Location loc2 = new Location(loc.getWorld(), loc.getBlockX()+0.5, block.getBoundingBox().getCenter().toLocation(block.getWorld()).getY(), loc.getBlockZ()+0.5);

        return createSecondPoint(player, loc2,true);
    }

    public static boolean createSecondPoint(Player player, Location loc, boolean onFence) {
        Bat leBat = findBatOnPLayer(player);
        if(leBat != null) {
            //Move the bat
            player.removePassenger(leBat);
            leBat.setAwake(true);
            if(onFence) {
                leBat.teleport(loc.subtract(0,0.265,0));
            } else {
                leBat.teleport(loc.add(0,0.15,0));
            }

            //Get other bat
            Bat leFirstBat = (Bat) ((StatValue)leBat.getMetadata("leLeash").get(0)).getValue();

            batMap.put(leFirstBat.getUniqueId(),new BatContainer(true,leBat.getUniqueId(),leFirstBat.getUniqueId()));
            batMap.put(leBat.getUniqueId(), new BatContainer(false,leFirstBat.getUniqueId(),leBat.getUniqueId()));

            //Make both persistent
            leBat.setPersistent(true);
            leFirstBat.setPersistent(true);

            //Remove metadata of first block if it has it
            if(leFirstBat.getLocation().getBlock().hasMetadata("inLeashing")) {
                leFirstBat.getLocation().getBlock().removeMetadata("inLeashing",JaklUtils.plugin);
            }

            player.removeMetadata("inLeashing",JaklUtils.plugin);
            return true;
        }
        return false;
    }

    public static boolean createHitch(Block block) {
        if(!block.getType().name().toLowerCase().contains("fence")) {
            return false;
        }
        for(Entity entity: block.getWorld().getNearbyEntities(block.getLocation(),0.5,0.5,0.5)) {
            if(entity instanceof LeashHitch) {
                return true;
            }
        }
        try {
            LeashHitch leHitch = (LeashHitch) block.getWorld().spawnEntity(block.getLocation(), EntityType.LEASH_HITCH);
            JaklUtils.plugin.persistent.addHitch(leHitch);
            leHitch.setMetadata("jaklHitch",new StatValue(null, JaklUtils.plugin));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Bat findBatOnPLayer(Player player) {
        for(Entity ent:player.getPassengers()) {
            if(ent.hasMetadata("leLeash") && ent instanceof Bat) {
                return (Bat) ent;
            }
        }
        return null;
    }

    public static void abortLeashing(Player player) {
        Bat leBat = findBatOnPLayer(player);

        //Get other bat
        Bat leFirstBat = (Bat) ((StatValue)leBat.getMetadata("leLeash").get(0)).getValue();

        //Remove metadata of first block if it has it
        if(leFirstBat.getLocation().getBlock().hasMetadata("inLeashing")) {
            leFirstBat.getLocation().getBlock().removeMetadata("inLeashing",JaklUtils.plugin);
        }

        removeLeash(leBat, leFirstBat, true, false);
    }

    public static void removeLeash(LeashHitch hitchy) {
        for(Entity ent:hitchy.getNearbyEntities(0.5,0.5,0.5)) {
            BatContainer leBatContainer = batMap.get(ent.getUniqueId());
            if(leBatContainer != null && leBatContainer.isFirst()) {
                removeLeash((Bat) ((Bat)ent).getLeashHolder(), (Bat) ent, false, true);
            }
            if(leBatContainer != null) {
                removeLeash((Bat) ent, (Bat) ent.getServer().getEntity(leBatContainer.getPartner()), true, false);
            }
        }
        deleteHitch(hitchy);
    }
    public static void removeLeash(Bat leBat) {
        BatContainer leBatContainer = batMap.get(leBat.getUniqueId());
        //Make sure first and second aren't swapped
        if(leBatContainer != null && leBatContainer.isFirst()) {
            removeLeash((Bat) leBat.getLeashHolder(), leBat, true, true);
        } else {
            removeLeash(leBat, (Bat) leBat.getServer().getEntity(leBatContainer.getPartner()), true, true);
        }
    }
    private static void removeLeash(Bat leSecondBat, Bat leFirstBat, boolean needsFirstCheck, boolean needsSecondCheck) {
        LeashHitch firstHitch = getHitch(leFirstBat);
        LeashHitch secondHitch = getHitch(leSecondBat);

        //Remove it from batMap
        batMap.remove(leFirstBat.getUniqueId());
        batMap.remove(leSecondBat.getUniqueId());

        leFirstBat.setLeashHolder(null);
        leSecondBat.remove();
        leFirstBat.remove();

        if(needsFirstCheck && firstHitch != null && canDeleteHitch(firstHitch)) {
            deleteHitch(firstHitch);
        }
        if(needsSecondCheck && secondHitch != null && canDeleteHitch(secondHitch)) {
            deleteHitch(secondHitch);
        }
    }

    private static void deleteHitch(LeashHitch leHitch) {
        JaklUtils.persistent.deleteHitch(leHitch);
        leHitch.remove();
    }

    private static LeashHitch getHitch(Bat bat) {
        for(Entity ent:bat.getNearbyEntities(0.5,0.5,0.5)) {
            if(ent.hasMetadata("jaklHitch") && ent instanceof LeashHitch) {
                return (LeashHitch) ent;
            }
        }
        return null;
    }
    private static boolean canDeleteHitch(LeashHitch hitch) {
        for(Entity ent:hitch.getNearbyEntities(0.5,0.5,0.5)) {
            if(isInBatMap(ent.getUniqueId())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInBatMap(UUID uuid) {
        if (batMap.containsKey(uuid)) {
            return true;
        }
        return false;
    }
    public static boolean isInBatMap(Entity ent) {
        if (batMap.containsKey(ent.getUniqueId())) {
            return true;
        }
        return false;
    }
    public static HashMap<UUID, BatContainer> getBatMap() {
        return batMap;
    }
    public static void setBatMap(HashMap<UUID,BatContainer> map) {
        batMap = map;
    }
}
