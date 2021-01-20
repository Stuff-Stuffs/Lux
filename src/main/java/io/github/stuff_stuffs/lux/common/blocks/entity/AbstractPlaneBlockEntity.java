package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.BeamInteraction;
import io.github.stuff_stuffs.lux.common.api.LuxReceiver;
import io.github.stuff_stuffs.lux.common.util.math.VecUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractPlaneBlockEntity<ThisType extends AbstractPlaneBlockEntity<ThisType>> extends BlockEntity implements BlockEntityClientSerializable {
    private static final double RADIUS = 0.5;
    private final LuxReceiver luxReceiver;
    private final Vec3d planeOrigin;
    private Vec3d planeNormal;

    public AbstractPlaneBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos blockPos, final BlockState blockState, final BeamInteraction<? super ThisType> frontInteraction, final BeamInteraction<? super ThisType> backInteraction) {
        super(blockEntityType, blockPos, blockState);
        planeOrigin = Vec3d.ofCenter(blockPos);
        planeNormal = new Vec3d(0, 0, 1);
        luxReceiver = (luxOrb, pos, world) -> {
            final BeamInteraction.CollisionResult collisionResult = BeamInteraction.getCollision(luxOrb, planeOrigin, planeNormal, RADIUS);
            if (collisionResult != null) {
                if (collisionResult.planeSide == VecUtil.PlaneSide.FRONT) {
                    return frontInteraction.onCollision(luxOrb, pos, world, planeNormal, planeOrigin, collisionResult, (ThisType) AbstractPlaneBlockEntity.this);
                } else if (collisionResult.planeSide == VecUtil.PlaneSide.BACK) {
                    return backInteraction.onCollision(luxOrb, pos, world, planeNormal, planeOrigin, collisionResult, (ThisType) AbstractPlaneBlockEntity.this);
                }
            }
            return null;
        };
    }


    public LuxReceiver getLuxReceiver() {
        return luxReceiver;
    }

    public Vec3d getPlaneNormal() {
        return planeNormal;
    }

    public void setPlaneNormal(final Vec3d planeNormal) {
        if (planeNormal.equals(Vec3d.ZERO)) {
            throw new RuntimeException();
        }
        this.planeNormal = planeNormal.normalize();
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
        tag.putDouble("xNorm", planeNormal.x);
        tag.putDouble("yNorm", planeNormal.y);
        tag.putDouble("zNorm", planeNormal.z);
        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        super.fromTag(tag);
        setPlaneNormal(new Vec3d(tag.getDouble("xNorm"), tag.getDouble("yNorm"), tag.getDouble("zNorm")));
    }

    @Override
    public void fromClientTag(final CompoundTag tag) {
        setPlaneNormal(new Vec3d(tag.getDouble("xNorm"), tag.getDouble("yNorm"), tag.getDouble("zNorm")));
    }

    @Override
    public CompoundTag toClientTag(final CompoundTag tag) {
        tag.putDouble("xNorm", planeNormal.x);
        tag.putDouble("yNorm", planeNormal.y);
        tag.putDouble("zNorm", planeNormal.z);
        return tag;
    }
}
