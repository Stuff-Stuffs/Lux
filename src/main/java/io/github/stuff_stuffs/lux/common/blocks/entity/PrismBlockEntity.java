package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.Constants;
import io.github.stuff_stuffs.lux.common.api.LuxReceiver;
import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import io.github.stuff_stuffs.lux.common.lux.LuxType;
import io.github.stuff_stuffs.lux.common.util.math.ImmutableMatrix3d;
import io.github.stuff_stuffs.lux.common.util.math.QuaternionD;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Random;

public class PrismBlockEntity extends AbstractAxisBlockEntity {
    private final Random random;
    private final LuxReceiver luxReceiver;

    public PrismBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.PRISM_BLOCK_ENTITY_TYPE, blockPos, blockState);
        random = new Random(blockPos.asLong());
        luxReceiver = (luxOrb, pos, world) -> {
            final BlockHitResult hitResult = getCachedState().getCollisionShape(world, pos, luxOrb.getShapeContext()).raycast(luxOrb.getPos(), luxOrb.getPos().add(luxOrb.getVelocity()), pos);
            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                final Vec3d dir = luxOrb.getVelocity().normalize();
                if (Math.abs(dir.dotProduct(getAxis())) > 0.999) {
                    return null;
                } else {
                    final double first = -Constants.PRISM_ANGLE;
                    final double last = Constants.PRISM_ANGLE;
                    final Collection<Pair<LuxSpectrum, LuxType>> splits = LuxSpectrum.noisySplit(luxOrb.getSpectrum(), Constants.PRISM_NOISE_FACTOR_LOWER, Constants.PRISM_NOISE_FACTOR_UPPER, random);
                    for (final Pair<LuxSpectrum, LuxType> split : splits) {
                        final double progress = split.getRight().ordinal() / (double) (LuxType.LUX_TYPE_COUNT - 1);
                        final double angle = MathHelper.lerp(progress, first, last);
                        emitRotated(hitResult.getPos(), dir, getAxis(), luxOrb.getFocus(), angle, split.getLeft(), world);
                    }
                    return hitResult.getPos();
                }
            }
            return null;
        };
    }

    public LuxReceiver getLuxReceiver() {
        return luxReceiver;
    }

    private static void emitRotated(final Vec3d pos, final Vec3d direction, final Vec3d axis, final float focus, final double angle, final LuxSpectrum spectrum, final World world) {
        final QuaternionD quaternionD = new QuaternionD(axis, angle, true);
        final ImmutableMatrix3d matrix3d = new ImmutableMatrix3d(quaternionD);
        final Vec3d outDir = matrix3d.transform(direction);
        LuxOrb.create(world, pos, outDir, focus, spectrum);
    }
}
