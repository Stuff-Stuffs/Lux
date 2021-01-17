package io.github.stuff_stuffs.lux.common.lux;

import com.google.common.base.Preconditions;
import io.github.stuff_stuffs.lux.common.util.HSVColour;
import io.github.stuff_stuffs.lux.common.util.RGBColour;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

import java.util.EnumMap;

public class LuxSpectrum {
    public static final LuxSpectrum EMPTY_SPECTRUM;
    public static final LuxSpectrum WHITE_SPECTRUM;

    private final EnumMap<LuxType, Float> fractions;
    private final float sum;

    private LuxSpectrum(final EnumMap<LuxType, Float> fractions) {
        this.fractions = fractions;
        float s = 0;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            final Float f = fractions.get(luxType);
            if (f != null) {
                s += f;
            }
        }
        sum = s;
    }

    public static LuxSpectrum scale(final LuxSpectrum spectrum, final float amount) {
        LuxSpectrum out = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            out = out.with(luxType, spectrum.getAmount(luxType) * amount);
        }
        return out;
    }

    public float getBrightness() {
        return sum;
    }

    public float getPurity() {
        if (sum == 0) {
            return 0;
        }
        float max = Float.NEGATIVE_INFINITY;
        for (final LuxType type : LuxType.LUX_TYPES) {
            final float v = getAmount(type);
            if (v > max) {
                max = v;
            }
        }
        return max / sum;
    }

    public float getTotal() {
        return sum;
    }

    public float getAmount(final LuxType luxType) {
        final Float f = fractions.get(luxType);
        return f == null ? 0 : f;
    }

    private int[] toIntArray() {
        final int[] integers = new int[LuxType.LUX_TYPE_COUNT];
        int i = 0;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            integers[i++] = Float.floatToIntBits(getAmount(luxType));
        }
        return integers;
    }

    public Tag toTag() {
        return new IntArrayTag(toIntArray());
    }

    public void toBuf(final PacketByteBuf buf) {
        buf.writeIntArray(toIntArray());
    }

    public RGBColour toColour() {
        float r = 0;
        float g = 0;
        float b = 0;
        double alpha = MathHelper.clamp(sum/(double)100, 0.5, 1);
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            final float amount = getAmount(luxType);
            r = Math.min(255, r + (luxType.getColour().getR() * amount));
            g = Math.min(255, g + (luxType.getColour().getG() * amount));
            b = Math.min(255, b + (luxType.getColour().getB() * amount));
        }
        HSVColour hsvColour = new RGBColour((int) r, (int) g, (int) b, (int) (alpha*255)).toHSV();
        HSVColour bright = new HSVColour(hsvColour.getH(), hsvColour.getS(), 1);
        return bright.toRgb((int) (alpha*255));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LuxSpectrum spectrum = (LuxSpectrum) o;

        return fractions.equals(spectrum.fractions);
    }

    @Override
    public int hashCode() {
        return fractions.hashCode() + 1;
    }

    private static LuxSpectrum fromIntArray(final int[] integers) {
        final EnumMap<LuxType, Float> fractions = new EnumMap<>(LuxType.class);
        for (int i = 0; i < integers.length; i++) {
            fractions.put(LuxType.byId(i), Float.intBitsToFloat(integers[i]));
        }
        return new LuxSpectrum(fractions);
    }

    public static LuxSpectrum fromTag(final Tag tag) {
        final int[] integers = ((IntArrayTag) tag).getIntArray();
        return fromIntArray(integers);
    }

    public static LuxSpectrum fromBuf(final PacketByteBuf buf) {
        final int[] integers = buf.readIntArray();
        return fromIntArray(integers);
    }

    public LuxSpectrum with(final LuxType luxType, final float amount) {
        Preconditions.checkArgument(0 <= amount);
        final EnumMap<LuxType, Float> newMap = new EnumMap<>(fractions);
        newMap.put(luxType, amount);
        return new LuxSpectrum(newMap);
    }

    public static LuxSpectrum mix(final LuxSpectrum first, final LuxSpectrum second, final float w1, final float w2) {
        assert w1 > 0 && w2 > 0;
        final EnumMap<LuxType, Float> newMap = new EnumMap<>(LuxType.class);
        final float w = w1 + w2;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            newMap.put(luxType, first.getAmount(luxType) / w * w1 + second.getAmount(luxType) / w * w2);
        }
        return new LuxSpectrum(newMap);
    }

    public static LuxSpectrum add(final LuxSpectrum first, final LuxSpectrum second) {
        LuxSpectrum lux = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            lux = lux.with(luxType, first.getAmount(luxType) + second.getAmount(luxType));
        }
        return lux;
    }

    public static LuxSpectrum subtract(final LuxSpectrum first, final LuxSpectrum second) {
        LuxSpectrum lux = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            lux = lux.with(luxType, Math.max(first.getAmount(luxType) - second.getAmount(luxType), 0));
        }
        return lux;
    }

    public static LuxSpectrum saturatingAdd(LuxSpectrum first, LuxSpectrum second, final float max) {
        Preconditions.checkArgument(0<max);
        LuxSpectrum lux = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            lux = lux.with(luxType, Math.min(first.getAmount(luxType) + second.getAmount(luxType), max));
        }
        return lux;
    }

    public static LuxSpectrum saturate(LuxSpectrum spectrum, final float max) {
        Preconditions.checkArgument(0 <= max);
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            spectrum = spectrum.with(luxType, Math.min(spectrum.getAmount(luxType), max));
        }
        return spectrum;
    }

    public static final class Filtered {
        private final LuxSpectrum passed;
        private final LuxSpectrum filtered;

        Filtered(final LuxSpectrum passed, final LuxSpectrum filtered) {
            this.passed = passed;
            this.filtered = filtered;
        }

        public LuxSpectrum getPassed() {
            return passed;
        }

        public LuxSpectrum getFiltered() {
            return filtered;
        }
    }

    public static Filtered filter(final LuxSpectrum lux, final LuxSpectrum filter) {
        LuxSpectrum out = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            out = out.with(luxType, lux.getAmount(luxType) * filter.getAmount(luxType));
        }
        return new Filtered(out, subtract(lux, out));
    }

    static {
        EMPTY_SPECTRUM = new LuxSpectrum(new EnumMap<>(LuxType.class));
        final EnumMap<LuxType, Float> map = new EnumMap<>(LuxType.class);
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            map.put(luxType, 1f);
        }
        WHITE_SPECTRUM = new LuxSpectrum(map);
    }
}
