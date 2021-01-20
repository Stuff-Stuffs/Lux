package io.github.stuff_stuffs.lux.common.api;

import io.github.stuff_stuffs.lux.common.blocks.entity.AbstractPlaneBlockEntity;
import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import io.github.stuff_stuffs.lux.common.util.math.VecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface BeamInteraction<T extends AbstractPlaneBlockEntity> {
    BeamInteraction<AbstractPlaneBlockEntity> REFLECT = (luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context) -> {
        final Vec3d direction = luxOrb.getVelocity().normalize();
        final Vec3d dir = direction.subtract(planeNormal.multiply(2 * planeNormal.dotProduct(direction))).normalize();
        LuxOrb.create(world, collisionResult.collisionPoint, dir, luxOrb.getFocus(), luxOrb.getSpectrum());
        return collisionResult.collisionPoint;
    };
    BeamInteraction<AbstractPlaneBlockEntity> PASS_THROUGH = (luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context) -> null;
    BeamInteraction<AbstractPlaneBlockEntity> HALF_REFLECT = (luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context) -> {
        final Vec3d direction = luxOrb.getVelocity().normalize();
        final Vec3d dir = direction.subtract(planeNormal.multiply(2 * planeNormal.dotProduct(direction))).normalize();
        final LuxSpectrum spectrum = LuxSpectrum.scale(luxOrb.getSpectrum(), 0.5f);
        LuxOrb.create(world, collisionResult.collisionPoint, dir, luxOrb.getFocus(), spectrum);
        LuxOrb.create(world, collisionResult.collisionPoint, direction, luxOrb.getFocus(), spectrum);
        return collisionResult.collisionPoint;
    };
    BeamInteraction<AbstractPlaneBlockEntity> BLOCK = (luxOrb, blockPos, world, planeNormal, planeOrigin, collisionResult, context) -> collisionResult.collisionPoint;

    Vec3d onCollision(final LuxOrb luxOrb, final BlockPos blockPos, final World world, Vec3d planeNormal, Vec3d planeOrigin, CollisionResult collisionResult, T context);

    static CollisionResult getCollision(final LuxOrb luxOrb, final Vec3d planeOrigin, final Vec3d planeNormal, final double planeRadius) {
        final double length = luxOrb.getVelocity().length();
        final Vec3d direction = luxOrb.getVelocity().multiply(1 / length);
        final VecUtil.RayPlaneIntersection rayPlaneIntersection = VecUtil.rayPlaneIntersection(luxOrb.getPos(), direction, planeOrigin, planeNormal);
        final double t = rayPlaneIntersection.getDelta();
        if (t > 0 && t <= length + 0.0001) {
            final Vec3d collision = luxOrb.getPos().add(direction.multiply(t));
            final Vec3d delta = collision.subtract(planeOrigin);
            if (VecUtil.chebyshevLength(delta) <= planeRadius) {
                return new CollisionResult(collision, rayPlaneIntersection.getSide());
            }
        }
        return null;
    }

    class CollisionResult {
        public final Vec3d collisionPoint;
        public final VecUtil.PlaneSide planeSide;

        public CollisionResult(final Vec3d collisionPoint, final VecUtil.PlaneSide planeSide) {
            this.collisionPoint = collisionPoint;
            this.planeSide = planeSide;
        }
    }
}
