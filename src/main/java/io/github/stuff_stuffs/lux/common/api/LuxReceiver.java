package io.github.stuff_stuffs.lux.common.api;

import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface LuxReceiver {
    void receive(LuxBeam luxBeam);

    @Nullable
    default Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final double length) {
        final Vec3d start = luxBeam.getPos();
        final Vec3d end = luxBeam.getPos().add(luxBeam.getDirection().multiply(length));
        final VoxelShape shape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
        final BlockHitResult result = shape.raycast(start, end, blockPos);
        if (result == null || result.getType() == HitResult.Type.MISS) {
            return null;
        } else {
            return result.getPos();
        }
    }
}
