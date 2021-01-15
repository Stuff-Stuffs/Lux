package io.github.stuff_stuffs.lux.common.util;

import net.minecraft.class_5575;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class EntityFilter<T extends Entity> implements class_5575<Entity, T> {
    private final Class<T> clazz;
    private final EntityType<T> entityType;

    public EntityFilter(Class<T> clazz, EntityType<T> entityType) {
        this.clazz = clazz;
        this.entityType = entityType;
    }

    @Nullable
    @Override
    public T method_31796(final Entity object) {
        return object.getType() == entityType ? (T) object : null;
    }

    @Override
    public Class<? extends Entity> method_31794() {
        return clazz;
    }
}
