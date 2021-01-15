package io.github.stuff_stuffs.lux.common.util;

import io.github.stuff_stuffs.lux.common.api.LuxApi;
import io.github.stuff_stuffs.lux.common.api.LuxReceiver;
import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public final class LuxBeamUtil {
    public static Vec3d castBeam(final LuxBeam beam, final double length, final World world, final boolean skipCurrentBlock) {
        final Vec3d start = beam.getPos();
        final Vec3d direction = beam.getDirection();
        final Vec3d end = start.add(direction.multiply(length));
        final BlockPos blockPos = new BlockPos(start);
        final CollisionUtil.Result result = CollisionUtil.rayCast(start, end, pos -> {
            if (skipCurrentBlock && pos.equals(blockPos)) {
                return null;
            }
            final BlockState state = world.getBlockState(pos);
            final VoxelShape voxelShape = state.getVisualShape(world, pos, ShapeContext.absent());
            if (!voxelShape.isEmpty()) {
                final BlockHitResult hitResult = voxelShape.raycast(start, end, pos);
                if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                    final LuxReceiver receiver = LuxApi.LUX_RECEIVER_BLOCK_LOOKUP.get(world, pos, null);
                    if (receiver != null) {
                        final Vec3d collision = receiver.getCollision(beam, pos, world, length);
                        if (collision == null) {
                            return null;
                        }
                        receiver.receive(beam);
                        return collision;
                    } else {
                        return hitResult.getPos();
                    }
                }
            }
            return null;
        });
        return result.getEndPos();
    }

    public static Box boxFromBeam(final LuxBeam luxBeam) {
        final Vec3d start = luxBeam.getPos();
        final Vec3d end = start.add(luxBeam.getDirection().multiply(luxBeam.getLength()));
        return new Box(start, end);
    }

    private LuxBeamUtil() {
    }
}
