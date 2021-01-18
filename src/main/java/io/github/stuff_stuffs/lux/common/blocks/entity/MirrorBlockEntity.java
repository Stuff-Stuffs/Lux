package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.BeamInteraction;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MirrorBlockEntity extends AbstractPlaneBlockEntity implements RenderAttachmentBlockEntity {
    public MirrorBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.MIRROR_BLOCK_ENTITY_TYPE, blockPos, blockState, BeamInteraction.REFLECT, BeamInteraction.REFLECT);
    }


    @Override
    public @Nullable Object getRenderAttachmentData() {
        return getPlaneNormal();
    }
}
