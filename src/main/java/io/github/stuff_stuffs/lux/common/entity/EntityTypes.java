package io.github.stuff_stuffs.lux.common.entity;

import io.github.stuff_stuffs.lux.common.Lux;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public final class EntityTypes {
    public static final EntityType<LuxOrbEntity> LUX_ORB_ENTITY_TYPE = register(FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<LuxOrbEntity>) (type, world) -> new LuxOrbEntity(world)).disableSummon().fireImmune().dimensions(EntityDimensions.fixed(0.1f, 0.1f)), "lux_orb");

    private static <T extends Entity> EntityType<T> register(final FabricEntityTypeBuilder<T> builder, final String name) {
        return Registry.register(Registry.ENTITY_TYPE, Lux.createId(name), builder.build());
    }

    private EntityTypes() {
    }

    public static void init() {
    }
}
