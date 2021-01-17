package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class EmitterBlockEntity extends BlockEntity {
    private int countDown = 40;

    public EmitterBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.EMITTER_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    public void tick() {
        countDown--;
        if (countDown == 0) {
            countDown = 40;
            Direction facing = getCachedState().get(Properties.FACING);
            Vec3d direction = new Vec3d(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ());
            LuxOrb.create(world, Vec3d.ofCenter(getPos()), direction, 0.95f, LuxSpectrum.WHITE_SPECTRUM);
        }
    }
}
