package de.jakllp.jaklutils.leashing;

import de.jakllp.jaklutils.helpers.customdatatypes.StatValue;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

public class LeashController {
    public static boolean createFirstPoint(Player player) {
        return createFirstPoint(player, player.getLocation());
    }

    public static boolean createFirstPoint(Player player, Block block) {
        return createFirstPoint(player, block.getBoundingBox().getCenter().toLocation(block.getWorld()).subtract(0,0,0.1875));
    }

    public static boolean createFirstPoint(Player player, Location loc) {
        if(player.isEmpty()) {
            //Spawn the bats
            Bat bat1 = (Bat) player.getWorld().spawnEntity(loc, EntityType.BAT);
            Bat bat2 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
            giveBatTreatment(bat1);
            giveBatTreatment(bat2);

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
        return createSecondPoint(player, player.getLocation());
    }

    public static boolean createSecondPoint(Player player, Block block) {
        return createSecondPoint(player, block.getBoundingBox().getCenter().toLocation(block.getWorld()).add(0,0.2,0));
    }

    public static boolean createSecondPoint(Player player, Location loc) {
        Bat leBat = findBatOnPLayer(player);
        if(leBat != null) {
            //Move the bat
            player.removePassenger(leBat);
            leBat.teleport(loc);

            //Get other bat
            Bat leFirstBat = (Bat) ((StatValue)leBat.getMetadata("leLeash").get(0)).getValue();
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

    public static boolean createHitch(Block block, boolean needsPersistent) {
        for(Entity entity: block.getWorld().getNearbyEntities(block.getLocation(),1,1,1)) {
            if(entity instanceof LeashHitch) {
                return true;
            }
        }
        try {
            LeashHitch leHitch = (LeashHitch) block.getWorld().spawnEntity(block.getLocation(), EntityType.LEASH_HITCH);
            if(needsPersistent) {
                JaklUtils.plugin.persistent.addHitch(leHitch);
            }
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
        for(Entity ent:hitchy.getNearbyEntities(1,1,1)) {
            if(ent.hasMetadata("leFirstBat")) {
                removeLeash((Bat) ((Bat)ent).getLeashHolder(), (Bat) ent, false, true);
            }
            if(ent.hasMetadata("leLeash")) {
                removeLeash((Bat) ent, (Bat) ((StatValue)ent.getMetadata("leLeash").get(0)).getValue(), true, false);
            }
        }
        deleteHitch(hitchy);
    }
    public static void removeLeash(Bat leBat) {
        //Make sure first and second aren't swapped
        if(leBat.hasMetadata("leFirstBat")) {
            removeLeash((Bat) leBat.getLeashHolder(), leBat, true, true);
        } else {
            removeLeash(leBat, (Bat) ((StatValue)leBat.getMetadata("leLeash").get(0)).getValue(), true, true);
        }
    }
    private static void removeLeash(Bat leSecondBat, Bat leFirstBat, boolean needsFirstCheck, boolean needsSecondCheck) {
        LeashHitch firstHitch = getHitch(leFirstBat);
        LeashHitch secondHitch = getHitch(leSecondBat);

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
        for(Entity ent:bat.getNearbyEntities(1,1,1)) {
            if(ent.hasMetadata("jaklHitch") && ent instanceof LeashHitch) {
                return (LeashHitch) ent;
            }
        }
        return null;
    }
    private static boolean canDeleteHitch(LeashHitch hitch) {
        for(Entity ent:hitch.getNearbyEntities(1,1,1)) {
            if(ent.hasMetadata("leLeash") || ent.hasMetadata("leFirstBat")) {
                return false;
            }
        }
        return true;
    }

    //TODO: Need to save bat pairs differently somehow... Metadata isn't persistent
}
