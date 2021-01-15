package io.github.stuff_stuffs.lux.common.util;

public final class Vec2d {
    private final double x;
    private final double y;

    public Vec2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double dot(final Vec2d other) {
        return x * other.x + y * other.y;
    }

    public Vec2d add(final Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }

    public Vec2d add(final double x, final double y) {
        return new Vec2d(this.x + x, this.y + y);
    }

    public Vec2d scale(final double x, final double y) {
        return new Vec2d(x * this.x, y * this.y);
    }

}
