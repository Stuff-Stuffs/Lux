package io.github.stuff_stuffs.lux.common.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public final class VecUtil {
    public static Vec3d projectOntoPlane(final Vec3d normal, final Vec3d planeOrigin, final Vec3d point) {
        final Vec3d delta = point.subtract(planeOrigin);
        final double scale = delta.dotProduct(normal);
        return point.subtract(normal.multiply(scale));
    }

    public static Vec3d lerp(final Vec3d first, final Vec3d second, final double delta) {
        return new Vec3d(first.x + (second.x - first.x) * delta, first.y + (second.y - first.y) * delta, first.z + (second.z - first.z) * delta);
    }

    public static Vec3d cross(final Vec3d first, final Vec3d second) {
        return new Vec3d(first.y * second.z - first.z * second.y, first.z * second.x - first.x * second.z, first.x * second.y - first.y * second.x);
    }

    public static Vec3d slerp(final Vec3d first, final Vec3d second, final double delta) {
        final double dot = MathHelper.clamp(first.dotProduct(second), -1, 1);
        final double theta = Math.acos(dot) * delta;
        final Vec3d relative = second.subtract(first.multiply(dot)).normalize();
        return first.multiply(Math.cos(theta)).add(relative.multiply(Math.sin(theta)));
    }

    public static RayPlaneIntersection rayPlaneIntersection(final Vec3d rayOrigin, final Vec3d rayDirection, final Vec3d planeOrigin, final Vec3d planeNormal) {
        final double dot = rayDirection.dotProduct(planeNormal);
        if (dot > 0.00001) {
            final Vec3d delta = planeOrigin.subtract(rayOrigin);
            return new RayPlaneIntersection(delta.dotProduct(planeNormal) / dot, PlaneSide.FRONT);
        }
        if (dot < 0.000001) {
            final Vec3d delta = planeOrigin.subtract(rayOrigin);
            return new RayPlaneIntersection(delta.dotProduct(planeNormal) / dot, PlaneSide.BACK);
        }
        return new RayPlaneIntersection(0, PlaneSide.INSIDE);
    }

    public static double chebyshevLength(final Vec3d vec) {
        return Math.max(Math.abs(vec.x), Math.max(Math.abs(vec.y), Math.abs(vec.z)));
    }

    public static Quaternion directionToQuaternion(final Vec3d direction) {
        final double pitch = Math.asin(-direction.y);
        final double yaw = MathHelper.atan2(direction.x, direction.z);
        final Quaternion quaternion = Quaternion.IDENTITY.copy();
        if (yaw != 0.0) {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) yaw));
        }

        if (pitch != 0.0) {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion((float) pitch));
        }
        return quaternion;
    }

    public static class RayPlaneIntersection {
        private final double t;
        private final PlaneSide side;

        public RayPlaneIntersection(final double t, final PlaneSide side) {
            this.t = t;
            this.side = side;
        }

        public double getDelta() {
            return t;
        }

        public PlaneSide getSide() {
            return side;
        }
    }

    public enum PlaneSide {
        FRONT,
        BACK,
        INSIDE
    }

    private VecUtil() {
    }
}
