package io.github.stuff_stuffs.lux.common.util;

import com.google.common.base.Preconditions;

public final class HSVColour {
    private final double h;
    private final double s;
    private final double v;

    public HSVColour(final double h, final double s, final double v) {
        Preconditions.checkArgument(0 <= h && h <= 360);
        Preconditions.checkArgument(0 <= s && s <= 1);
        Preconditions.checkArgument(0 <= v && v <= 1);
        this.h = h;
        this.s = s;
        this.v = v;
    }

    public RGBColour toRgb(final int a) {
        if (s <= 0.0) {
            return new RGBColour((int) (v * 255), (int) (v * 255), (int) (v * 255), a);
        }
        double hh = h;
        if (hh >= 360.0) {
            hh = 0.0;
        }
        hh /= 60.0;
        final int i = (int) hh;
        final double ff = hh - i;
        final double p = v * (1.0 - s);
        final double q = v * (1.0 - (s * ff));
        final double t = v * (1.0 - (s * (1.0 - ff)));
        switch (i) {
            case 0:
                return new RGBColour((int) (v * 255), (int) (t * 255), (int) (p * 255));
            case 1:
                return new RGBColour((int) (q * 255), (int) (v * 255), (int) (p * 255));
            case 2:
                return new RGBColour((int) (p * 255), (int) (v * 255), (int) (t * 255));
            case 3:
                return new RGBColour((int) (p * 255), (int) (q * 255), (int) (v * 255));
            case 4:
                return new RGBColour((int) (t * 255), (int) (p * 255), (int) (v * 255));
            default:
                return new RGBColour((int) (v * 255), (int) (p * 255), (int) (q * 255));
        }
    }

    public double getH() {
        return h;
    }

    public double getS() {
        return s;
    }

    public double getV() {
        return v;
    }
}
