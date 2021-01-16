package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class EmitterBlockEntity extends BlockEntity {
    private LuxBeam.OwnedLuxBeam luxBeam;

    public EmitterBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.EMITTER_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    public void tick() {
        if (luxBeam == null) {
            final BlockState state = getCachedState();
            final Direction direction = state.get(Properties.FACING);
            final Vec3d dir = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
            luxBeam = LuxBeam.create(Vec3d.ofCenter(getPos()), dir, 0.8, LuxSpectrum.scale(LuxSpectrum.WHITE_SPECTRUM,100));
        }
        luxBeam.tick(world);
    }
}
