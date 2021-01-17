package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BrightnessMultiplierBlockEntity extends AbstractPlaneBlockEntity {
    public BrightnessMultiplierBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.BRIGHTNESS_MULTIPLIER_BLOCK_ENTITY_TYPE, blockPos, blockState, new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPlaneBlockEntity.CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void onCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPlaneBlockEntity.CollisionResult collisionResult) {
                LuxOrb.create(world, collisionResult.collisionPoint, luxOrb.getVelocity().normalize(), luxOrb.getFocus(), LuxSpectrum.scale(luxOrb.getSpectrum(), 2));
            }
        }, new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPlaneBlockEntity.CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void onCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPlaneBlockEntity.CollisionResult collisionResult) {
                LuxOrb.create(world, collisionResult.collisionPoint, luxOrb.getVelocity().normalize(), luxOrb.getFocus(), LuxSpectrum.scale(luxOrb.getSpectrum(), 2));
            }
        });
    }
}
