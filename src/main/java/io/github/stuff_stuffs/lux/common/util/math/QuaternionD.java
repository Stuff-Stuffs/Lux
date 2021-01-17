package io.github.stuff_stuffs.lux.common.util.math;

import net.minecraft.util.math.Vec3d;

public class QuaternionD {
    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public QuaternionD(final Vec3d axis, double rotationAngle, final boolean degrees) {
        if (degrees) {
            rotationAngle *= 0.017453292F;
        }

        final double f = Math.sin(rotationAngle / 2.0);
        x = axis.getX() * f;
        y = axis.getY() * f;
        z = axis.getZ() * f;
        w = Math.cos(rotationAngle / 2.0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }
}
