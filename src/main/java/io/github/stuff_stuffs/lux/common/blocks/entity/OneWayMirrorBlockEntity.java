package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.BeamInteraction;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class OneWayMirrorBlockEntity extends AbstractPlaneBlockEntity implements RenderAttachmentBlockEntity {
    public OneWayMirrorBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.ONE_WAY_MIRROR_BLOCK_ENTITY_TYPE, blockPos, blockState, BeamInteraction.REFLECT, BeamInteraction.PASS_THROUGH);
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return getPlaneNormal();
    }
}
