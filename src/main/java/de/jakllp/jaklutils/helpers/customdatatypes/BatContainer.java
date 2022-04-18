package de.jakllp.jaklutils.helpers.customdatatypes;

import java.io.Serializable;
import java.util.UUID;

public class BatContainer implements Serializable {
    private boolean first;
    private UUID partner;
    private UUID itself;

    public BatContainer(boolean first, UUID partner, UUID itself) {
        this.first=first;
        this.partner=partner;
        this.itself=itself;
    }

    public UUID getItself() {
        return itself;
    }

    public UUID getPartner() {
        return partner;
    }

    public boolean isFirst() {
        return first;
    }
}
