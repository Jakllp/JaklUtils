package de.jakllp.jaklutils.helpers.customdatatypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.UUID;

public class SerializableLocation implements Serializable {
    public UUID worldId;
    public int x;
    public int y;
    public int z;

    public SerializableLocation(Location loc) {
        this.worldId = loc.getWorld().getUID();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.worldId),this.x,this.y,this.z);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof SerializableLocation) {
            SerializableLocation leOther = (SerializableLocation) other;
            if(leOther.worldId==this.worldId && leOther.x==this.x && leOther.y==this.y && leOther.z==this.z) {
                return true;
            }
        }
        return false;
    }
}
