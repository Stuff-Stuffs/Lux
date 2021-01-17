package io.github.stuff_stuffs.lux.common.blocks.entity;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class HalfSilveredMirrorBlockEntity extends AbstractPlaneBlockEntity implements RenderAttachmentBlockEntity {
    public HalfSilveredMirrorBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.HALF_SILVERED_MIRROR_BLOCK_ENTITY_TYPE, blockPos, blockState, BeamInteraction.HALF_REFLECT, BeamInteraction.HALF_REFLECT);
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return getPlaneNormal();
    }
}
