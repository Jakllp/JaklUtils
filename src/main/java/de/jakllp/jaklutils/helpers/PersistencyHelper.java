package de.jakllp.jaklutils.helpers;

import de.jakllp.jaklutils.helpers.customdatatypes.SerializableLocation;
import de.jakllp.jaklutils.leashing.LeashController;
import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.entity.LeashHitch;

import java.io.*;
import java.util.ArrayList;
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

        //Read all LeashKnots
        try {
            File leFile = new File(this.plugin.getDataFolder(), "/data/LeashHitches");
            if (!leFile.exists()) {
                leFile.createNewFile();
            } else {
                FileInputStream fin= new FileInputStream(leFile);
                ObjectInputStream ois = new ObjectInputStream(fin);
                this.leashKnotList= (ArrayList<SerializableLocation>)ois.readObject();
                fin.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Add all LeashHitches back
        boolean actuallyRestored = false;
        for(SerializableLocation loc:leashKnotList) {
            actuallyRestored = true;
            LeashController.createHitch(loc.toLocation().getBlock(),false);
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
    }
    public void addHitch(LeashHitch hitch) {
        this.leashKnotList.add(new SerializableLocation(hitch.getLocation()));
    }
    public void deleteHitch(LeashHitch hitch) { this.leashKnotList.remove(new SerializableLocation(hitch.getLocation())); }

    //TODO: Add Persistency for metadata or something... Maybe save the pairs
}
