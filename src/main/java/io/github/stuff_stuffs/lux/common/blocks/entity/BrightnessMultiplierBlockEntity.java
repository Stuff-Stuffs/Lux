package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BrightnessMultiplierBlockEntity extends AbstractPlaneBlockEntity<BrightnessMultiplierBlockEntity> {
    public BrightnessMultiplierBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        super(BlockEntityTypes.BRIGHTNESS_MULTIPLIER_BLOCK_ENTITY_TYPE, blockPos, blockState, (luxOrb, blockPos1, world, planeNormal, planeOrigin, collisionResult, context) -> {
            LuxOrb.create(world, collisionResult.collisionPoint, luxOrb.getVelocity().normalize(), luxOrb.getFocus(), LuxSpectrum.scale(luxOrb.getSpectrum(), 2));
            return collisionResult.collisionPoint;
        }, (luxOrb, blockPos1, world, planeNormal, planeOrigin, collisionResult, context) -> {
            LuxOrb.create(world, collisionResult.collisionPoint, luxOrb.getVelocity().normalize(), luxOrb.getFocus(), LuxSpectrum.scale(luxOrb.getSpectrum(), 2));
            return collisionResult.collisionPoint;
        });
    }
}
