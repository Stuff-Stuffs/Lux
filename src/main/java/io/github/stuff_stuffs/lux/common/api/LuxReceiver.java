package io.github.stuff_stuffs.lux.common.api;

import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface LuxReceiver {
    void receive(LuxOrb luxOrb);

    @Nullable
    default Vec3d getCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world) {
        final Vec3d start = luxOrb.getPos();
        final Vec3d end = luxOrb.getPos().add(luxOrb.getVelocity());
        final VoxelShape shape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
        final BlockHitResult result = shape.raycast(start, end, blockPos);
        if (result == null || result.getType() == HitResult.Type.MISS) {
            return null;
        } else {
            return result.getPos();
        }
    }
}
