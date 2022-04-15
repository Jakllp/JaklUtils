package de.jakllp.jaklutils.helpers;

import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PersistencyHelper {
    List leashKnotList = new ArrayList<Location>();

    public PersistencyHelper() {
        //ToDo: Regain everything
    }

    public void savePersistent() {
        //ToDo: YAYY
    }
    public void addKnot(Location loc) {
        leashKnotList.add(loc);
    }
}
