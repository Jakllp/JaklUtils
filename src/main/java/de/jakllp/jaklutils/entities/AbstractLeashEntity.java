package de.jakllp.jaklutils.entities;

import de.jakllp.jaklutils.reflection.ReflectionUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.attribute.CraftAttributeMap;

import java.lang.reflect.Field;

public class AbstractLeashEntity extends Mob {
    protected AttributeMap attributeMap;

    protected AbstractLeashEntity(EntityType<? extends Mob> entitytypes, Level world) {
        super(entitytypes, world);
        this.replaceCraftAttributes();
    }

    protected void replaceCraftAttributes() {
        Field craftAttributesField = ReflectionUtil.getField(LivingEntity.class, "craftAttributes");
        CraftAttributeMap craftAttributes = new CraftAttributeMap(this.getAttributes());
        ReflectionUtil.setFinalFieldValue(craftAttributesField, this, craftAttributes);
    }

    public void setLocation(Location loc) {
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setRot(loc.getPitch(), loc.getYaw());
    }
}
