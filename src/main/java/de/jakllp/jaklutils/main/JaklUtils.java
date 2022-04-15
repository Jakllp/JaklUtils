package de.jakllp.jaklutils.main;

import de.jakllp.jaklutils.entities.LeashEntityType;
import de.jakllp.jaklutils.listeners.LeashListener;
import de.jakllp.jaklutils.reflection.ReflectionUtil;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class JaklUtils extends JavaPlugin {
    public static JaklUtils plugin;
    public static LeashListener leashListener = null;
    Map<LeashEntityType, EntityType> entityTypes = new HashMap<>();

    public EntityType getEntityType(LeashEntityType ehhh) {
        return entityTypes.get(ehhh);
    }

    @Override
    public void onEnable() {
        plugin = this;

        leashListener = new LeashListener(this);
        getServer().getPluginManager().registerEvents(leashListener, this);

        // HAHA ENTITY TYPES
        DefaultedRegistry<EntityType<?>> entityRegistry = getRegistry(Registry.ENTITY_TYPE);
        Field frozenDoBe = ReflectionUtil.getField(MappedRegistry.class,"bL"); //frozen
        Field intrusiveHolderCacheField = ReflectionUtil.getField(MappedRegistry.class,"bN"); //intrusiveHolderCache

        ReflectionUtil.setFinalFieldValue(frozenDoBe, entityRegistry, false);
        ReflectionUtil.setFinalFieldValue(intrusiveHolderCacheField, entityRegistry, new IdentityHashMap());

        registerEntityType("armor_stand",LeashEntityType.LeashArmorStand,entityRegistry);
        entityRegistry.freeze();

        Bukkit.getConsoleSender().sendMessage("JaklUtils loaded!");
    }

    @Override
    public void onDisable() {

    }

    protected void registerEntityType(String key, LeashEntityType leashEntityType, DefaultedRegistry<EntityType<?>> entityRegistry) {
        EntityDimensions size = entityRegistry.get(new ResourceLocation(key.toLowerCase())).getDimensions();
        entityTypes.put(leashEntityType, Registry.register(entityRegistry, "jaklutil_" + key.toLowerCase(), EntityType.Builder.createNothing(MobCategory.CREATURE).noSave().noSummon().sized(size.width, size.height).build(key)));
        EntityType<? extends LivingEntity> types = (EntityType<? extends LivingEntity>) entityRegistry.get(new ResourceLocation(key));
        overwriteEntityID(entityTypes.get(leashEntityType), getEntityTypeId(leashEntityType, entityRegistry), entityRegistry);
    }

    protected int getEntityTypeId(LeashEntityType type, DefaultedRegistry<EntityType<?>> entityRegistry) {
        EntityType<?> types = entityRegistry.get(new ResourceLocation(type.getTypeID().toString()));
        return entityRegistry.getId(types);
    }

    public DefaultedRegistry<EntityType<?>> getRegistry(DefaultedRegistry registryMaterials) {
        if (!registryMaterials.getClass().getName().equals(DefaultedRegistry.class.getName())) {
            for (Field field : registryMaterials.getClass().getDeclaredFields()) {
                if (field.getType() == MappedRegistry.class) {
                    field.setAccessible(true);
                    try {
                        DefaultedRegistry<EntityType<?>> reg = (DefaultedRegistry<EntityType<?>>) field.get(registryMaterials);

                        if (!reg.getClass().getName().equals(DefaultedRegistry.class.getName())) {
                            reg = getRegistry(reg);
                        }

                        return reg;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return registryMaterials;
    }

    protected void overwriteEntityID(EntityType types, int id, DefaultedRegistry<EntityType<?>> entityRegistry) {
        try {
            Field bgF = MappedRegistry.class.getDeclaredField("bE"); //This is toId
            bgF.setAccessible(true);
            Object map = bgF.get(entityRegistry);
            Class<?> clazz = map.getClass();
            Method mapPut = clazz.getDeclaredMethod("put", Object.class, int.class);
            mapPut.setAccessible(true);
            mapPut.invoke(map, types, id);
        } catch (ReflectiveOperationException ex) {

            ex.printStackTrace();
        }
    }
}
