package de.jakllp.jaklutils.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.UUID;

public class SerializableLocation implements Serializable {
    public UUID worldId;
    public double x;
    public double y;
    public double z;

    public SerializableLocation(Location loc) {
        this.worldId = loc.getWorld().getUID();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
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
