package de.jakllp.jaklutils.helpers;

import de.jakllp.jaklutils.helpers.customdatatypes.BatContainer;
import de.jakllp.jaklutils.helpers.customdatatypes.SerializableLocation;
import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LeashHitch;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PersistencyHelper {
    private static JaklUtils plugin;
    private List<SerializableLocation> leashKnotList = new ArrayList<>();

    public PersistencyHelper(JaklUtils plugin) {
        this.plugin = plugin;
        //Check for Folder and make one if needed
        File f = new File(this.plugin.getDataFolder() + "/");
        if(!f.exists())
            f.mkdir();
        File fd = new File(this.plugin.getDataFolder() + "/data");
        if(!fd.exists())
            fd.mkdir();
    }

    public void restorePersistent() {
        //Read all LeashKnots
        List<SerializableLocation> tempList = new ArrayList<>();
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/LeashHitches");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileInputStream fin= new FileInputStream(leFile);
                ObjectInputStream ois = new ObjectInputStream(fin);
                tempList = (ArrayList<SerializableLocation>)ois.readObject();
                fin.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Add all LeashHitches back
        boolean actuallyRestored = false;
        for(SerializableLocation loc:tempList) {
            actuallyRestored = true;
            LeashController.createHitch(loc.toLocation().getBlock());
        }

        //Read all Silent
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/SilentCreators");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileInputStream fin= new FileInputStream(leFile);
                ObjectInputStream ois = new ObjectInputStream(fin);
                this.plugin.silent= (ArrayList<UUID>)ois.readObject();
                fin.close();

                if(!this.plugin.silent.isEmpty())
                    actuallyRestored = true;
            }
        } catch(Exception e) {
            //Ignore
        }
        //Refill batMap
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/BatMap");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileInputStream fin= new FileInputStream(leFile);
                ObjectInputStream ois = new ObjectInputStream(fin);
                LeashController.setBatMap((HashMap<UUID, BatContainer>)ois.readObject());
                fin.close();

                if(!LeashController.getBatMap().isEmpty())
                    actuallyRestored = true;
            }
        } catch(Exception e) {
            //Ignore
        }
        if(actuallyRestored)
            this.plugin.logger.info("Restored persistent data");
    }

    public void savePersistent() {
        //Write all Hitches
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/LeashHitches");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileOutputStream fout= new FileOutputStream(leFile);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(this.leashKnotList);
                fout.close();
            }
        } catch(Exception e) {
            //Ignore
        }

        //Write all Silent
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/SilentCreators");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileOutputStream fout= new FileOutputStream(leFile);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(this.plugin.silent);
                fout.close();
            }
        } catch(Exception e) {
            //Ignore
        }

        //Write BatMap
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/BatMap");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileOutputStream fout= new FileOutputStream(leFile);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(LeashController.getBatMap());
                fout.close();
            }
        } catch(Exception e) {
            //Ignore
        }
    }

    public void addHitch(LeashHitch hitch) {
        this.leashKnotList.add(new SerializableLocation(hitch.getLocation()));
    }
    public void deleteHitch(LeashHitch hitch) {this.leashKnotList.remove(new SerializableLocation(hitch.getLocation()));}
}
