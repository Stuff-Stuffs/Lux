package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.BeamInteraction;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class ThresholdingMirrorBlockEntity extends AbstractPlaneBlockEntity<ThresholdingMirrorBlockEntity> {
    public static BeamInteraction<ThresholdingMirrorBlockEntity> THRESHOLDING_INTERACTION = (luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context) -> {
        if (luxOrb.getSpectrum().getBrightness() > context.getThreshold()) {
            return BeamInteraction.PASS_THROUGH.onCollision(luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context);
        } else {
            return BeamInteraction.REFLECT.onCollision(luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context);
        }
    };
    private float threshold = 1;

    public ThresholdingMirrorBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.THRESHOLDING_MIRROR_BLOCK_ENTITY_TYPE, blockPos, blockState, THRESHOLDING_INTERACTION, THRESHOLDING_INTERACTION);
    }

    public void setThreshold(final float threshold) {
        this.threshold = threshold;
        if (world != null) {
            if (!world.isClient()) {
                sync();
            }
        }
    }

    public float getThreshold() {
        return threshold;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putFloat("threshold", threshold);
        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        super.fromTag(tag);
        setThreshold(tag.getFloat("threshold"));
    }
}
