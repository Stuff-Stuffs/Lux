package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class BrightnessMultiplierBlockEntity extends AbstractPaneBlockEntity {
    public BrightnessMultiplierBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.BRIGHTNESS_MULTIPLIER_BLOCK_ENTITY_TYPE, blockPos, blockState, new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPaneBlockEntity.CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPaneBlockEntity.CollisionResult collisionResult, final Consumer<Incoming> consumer) {
                consumer.accept(new Incoming(luxBeam.getDirection(), collisionResult.collisionPoint, luxBeam.getFocus(), LuxSpectrum.scale(luxBeam.getSpectrum(), 2)));
            }
        }, new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPaneBlockEntity.CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final AbstractPaneBlockEntity.CollisionResult collisionResult, final Consumer<Incoming> consumer) {
                consumer.accept(new Incoming(luxBeam.getDirection(), collisionResult.collisionPoint, luxBeam.getFocus(), LuxSpectrum.scale(luxBeam.getSpectrum(), 2)));
            }
        });
    }
}
