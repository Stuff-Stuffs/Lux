package io.github.stuff_stuffs.lux.common.util.math;

import net.minecraft.util.math.Vec3d;

public class ImmutableMatrix3d {
    private final double a00;
    private final double a01;
    private final double a02;
    private final double a10;
    private final double a11;
    private final double a12;
    private final double a20;
    private final double a21;
    private final double a22;

    public ImmutableMatrix3d(final double a00, final double a01, final double a02, final double a10, final double a11, final double a12, final double a20, final double a21, final double a22) {
        this.a00 = a00;
        this.a01 = a01;
        this.a02 = a02;
        this.a10 = a10;
        this.a11 = a11;
        this.a12 = a12;
        this.a20 = a20;
        this.a21 = a21;
        this.a22 = a22;
    }

    public ImmutableMatrix3d(final QuaternionD quaternion) {
        final double f = quaternion.getX();
        final double g = quaternion.getY();
        final double h = quaternion.getZ();
        final double i = quaternion.getW();
        final double j = 2.0F * f * f;
        final double k = 2.0F * g * g;
        final double l = 2.0F * h * h;
        a00 = 1.0F - k - l;
        a11 = 1.0F - l - j;
        a22 = 1.0F - j - k;
        final double m = f * g;
        final double n = g * h;
        final double o = h * f;
        final double p = f * i;
        final double q = g * i;
        final double r = h * i;
        a10 = 2.0F * (m + r);
        a01 = 2.0F * (m - r);
        a20 = 2.0F * (o - q);
        a02 = 2.0F * (o + q);
        a21 = 2.0F * (n + p);
        a12 = 2.0F * (n - p);
    }

    public ImmutableMatrix3d multiply(final ImmutableMatrix3d other) {
        final double a00 = this.a00 * other.a00 + a01 * other.a10 + a02 * other.a20;
        final double a01 = this.a00 * other.a01 + this.a01 * other.a11 + a02 * other.a21;
        final double a02 = this.a00 * other.a02 + this.a01 * other.a12 + this.a02 * other.a22;
        final double a10 = this.a10 * other.a00 + a11 * other.a10 + a12 * other.a20;
        final double a11 = this.a10 * other.a01 + this.a11 * other.a11 + a12 * other.a21;
        final double a12 = this.a10 * other.a02 + this.a11 * other.a12 + this.a12 * other.a22;
        final double a20 = this.a20 * other.a00 + a21 * other.a10 + a22 * other.a20;
        final double a21 = this.a20 * other.a01 + this.a21 * other.a11 + a22 * other.a21;
        final double a22 = this.a20 * other.a02 + this.a21 * other.a12 + this.a22 * other.a22;
        return new ImmutableMatrix3d(a00, a01, a02, a10, a11, a12, a20, a21, a22);
    }

    public Vec3d transform(final Vec3d v) {
        return new Vec3d(a00 * v.x + a01 * v.y + a02 * v.z, a10 * v.x + a11 * v.y + a12 * v.z, a20 * v.x + a21 * v.y + a22 * v.z);
    }
}
