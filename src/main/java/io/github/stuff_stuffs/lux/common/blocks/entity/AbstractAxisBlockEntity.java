package io.github.stuff_stuffs.lux.common.blocks.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractAxisBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private Vec3d axis;

    public AbstractAxisBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos blockPos, final BlockState blockState) {
        this(blockEntityType, blockPos, blockState, new Vec3d(1, 0, 0));
    }

    public AbstractAxisBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos blockPos, final BlockState blockState, final Vec3d startingAxis) {
        super(blockEntityType, blockPos, blockState);
        if (startingAxis.equals(Vec3d.ZERO)) {
            throw new RuntimeException();
        }
        axis = startingAxis.normalize();
    }

    public Vec3d getAxis() {
        return axis;
    }

    public void setAxis(final Vec3d axis) {
        if (axis.equals(Vec3d.ZERO)) {
            throw new RuntimeException();
        }
        this.axis = axis.normalize();
        markDirty();
        if (world != null) {
            if (!world.isClient()) {
                sync();
            } else {
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 2);
            }
        }
    }

    @Override
    public void markDirty() {
        if (world != null) {
            world.markDirty(getPos());
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putDouble("xAxis", axis.x);
        tag.putDouble("yAxis", axis.y);
        tag.putDouble("zAxis", axis.z);
        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        super.fromTag(tag);
        setAxis(new Vec3d(tag.getDouble("xAxis"), tag.getDouble("yAxis"), tag.getDouble("zAxis")));
    }

    @Override
    public void fromClientTag(final CompoundTag tag) {
        setAxis(new Vec3d(tag.getDouble("xAxis"), tag.getDouble("yAxis"), tag.getDouble("zAxis")));
    }

    @Override
    public CompoundTag toClientTag(final CompoundTag tag) {
        tag.putDouble("xAxis", axis.x);
        tag.putDouble("yAxis", axis.y);
        tag.putDouble("zAxis", axis.z);
        return tag;
    }

}
