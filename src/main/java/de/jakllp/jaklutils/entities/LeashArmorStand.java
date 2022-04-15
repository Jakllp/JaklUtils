package de.jakllp.jaklutils.entities;

import de.jakllp.jaklutils.main.JaklUtils;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class LeashArmorStand extends AbstractLeashEntity {
    static private AttributeSupplier leSupplier = Horse.createBaseHorseAttributes().build();

    public LeashArmorStand(Level world) {
        super(JaklUtils.plugin.getEntityType(LeashEntityType.LeashArmorStand), world);
    }

    @Override
    public AttributeMap getAttributes() {
        if (attributeMap == null) {
            this.attributeMap = new AttributeMap(leSupplier);
        }
        return attributeMap;
    }
}
