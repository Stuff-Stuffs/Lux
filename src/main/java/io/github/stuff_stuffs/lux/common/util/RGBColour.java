package io.github.stuff_stuffs.lux.common.util;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;

public final class RGBColour {
    public static final Codec<RGBColour> CODEC;
    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public RGBColour(final int argb) {
        this((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (argb >> 24) & 255);
    }

    public RGBColour(final int r, final int g, final int b) {
        this(r, g, b, 255);
    }

    public RGBColour(final int r, final int g, final int b, final int a) {
        Preconditions.checkArgument(checkValid(r));
        Preconditions.checkArgument(checkValid(g));
        Preconditions.checkArgument(checkValid(b));
        Preconditions.checkArgument(checkValid(a));
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
        return a;
    }

    private static boolean checkValid(final int b) {
        return 0 <= b && b <= 255;
    }

    public int toInt() {
        return ((a << 8 | r) << 8 | g) << 8 | b;
    }

    public void toBuf(final PacketByteBuf buf) {
        buf.writeByte(r);
        buf.writeByte(g);
        buf.writeByte(b);
        buf.writeByte(a);
    }

    public static RGBColour lerp(final RGBColour first, final RGBColour second, final double delta) {
        Preconditions.checkArgument(0 <= delta, "delta must be greater than or equal to 0");
        Preconditions.checkArgument(delta <= 1, "delta must be less than or equal to 1");

        final HSVColour firstHSV = first.toHSV();
        final HSVColour secondHSV = second.toHSV();
        final double h = firstHSV.getH() + (secondHSV.getH() - firstHSV.getH()) * delta;
        final double s = firstHSV.getS() + (secondHSV.getS() - firstHSV.getS()) * delta;
        final double v = firstHSV.getV() + (secondHSV.getV() - firstHSV.getV()) * delta;
        final int a = first.getA() + (int) ((second.getA() - first.getA()) * delta);
        return new HSVColour(h, s, v).toRgb(a);
    }

    public HSVColour toHSV() {
        final double inR = r / 255d;
        final double inG = g / 255d;
        final double inB = b / 255d;
        final double min = Math.min(r, Math.min(g, b)) / 255d;
        final double max = Math.max(r, Math.max(g, b)) / 255d;
        final double delta = max - min;

        final double s;
        double h;
        if (delta < 0.00001) {
            return new HSVColour(0, 0, max);
        }
        if (max > 0.0) {
            s = (delta / max);
        } else {
            return new HSVColour(0, 0, max);
        }
        if (inR >= max) {
            h = (inG - inB) / delta;
        } else if (inG >= max) {
            h = 2.0 + (inB - inR) / delta;
        } else {
            h = 4.0 + (inR - inG) / delta;
        }
        h *= 60.0;
        if (h < 0.0) {
            h += 360.0;
        }
        return new HSVColour(h, s, max);
    }

    public static RGBColour fromBuf(final PacketByteBuf buf) {
        return new RGBColour(buf.readByte() - Byte.MIN_VALUE, buf.readByte() - Byte.MIN_VALUE, buf.readByte() - Byte.MIN_VALUE, buf.readByte() - Byte.MIN_VALUE);
    }

    @Override
    public String toString() {
        return "Colour{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 256).fieldOf("r").forGetter(RGBColour::getR),
                Codec.intRange(0, 256).fieldOf("g").forGetter(RGBColour::getG),
                Codec.intRange(0, 256).fieldOf("b").forGetter(RGBColour::getB),
                Codec.intRange(0, 256).fieldOf("a").forGetter(RGBColour::getA)).
                apply(instance, RGBColour::new));
    }
}
