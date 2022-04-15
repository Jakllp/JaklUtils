package de.jakllp.jaklutils.entities;

public enum LeashEntityType {
    LeashArmorStand("armor_stand");

    private final Object typeID;

    LeashEntityType(String id) {
        this.typeID = id;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypeID() {
        return (T) typeID;
    }
}
