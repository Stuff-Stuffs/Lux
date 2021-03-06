package io.github.stuff_stuffs.lux.common.lux;

import com.google.common.base.Preconditions;
import io.github.stuff_stuffs.lux.common.Constants;
import io.github.stuff_stuffs.lux.common.util.HSVColour;
import io.github.stuff_stuffs.lux.common.util.RGBColour;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class LuxSpectrum {
    public static final LuxSpectrum EMPTY_SPECTRUM;
    public static final LuxSpectrum WHITE_SPECTRUM;

    private final EnumMap<LuxType, Float> fractions;
    private final float sum;

    private LuxSpectrum(final EnumMap<LuxType, Float> fractions, final float sum) {
        this.fractions = fractions;
        this.sum = sum;
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
        final double alpha = MathHelper.clamp(sum / (double) 100, 0.5, 1);
        float sum = 0;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            final float amount = getAmount(luxType);
            sum += amount;
            r = r + luxType.getColour().getR() * amount;
            g = g + luxType.getColour().getG() * amount;
            b = b + luxType.getColour().getB() * amount;
        }
        r /= sum;
        g /= sum;
        b /= sum;
        final HSVColour hsvColour = new RGBColour((int) r, (int) g, (int) b, (int) (alpha * 255)).toHSV();
        final HSVColour bright = new HSVColour(hsvColour.getH(), hsvColour.getS(), 1);
        return bright.toRgb((int) (alpha * 255));
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
        float sum = 0;
        for (final Map.Entry<LuxType, Float> entry : fractions.entrySet()) {
            sum += entry.getValue();
        }
        return new LuxSpectrum(fractions, sum);
    }

    public static LuxSpectrum fromTag(final Tag tag) {
        final int[] integers = ((IntArrayTag) tag).getIntArray();
        return fromIntArray(integers);
    }

    public static LuxSpectrum fromBuf(final PacketByteBuf buf) {
        final int[] integers = buf.readIntArray();
        return fromIntArray(integers);
    }

    public LuxSpectrum with(final LuxType luxType, float amount) {
        Preconditions.checkArgument(0 <= amount);
        amount = Math.min(amount, Constants.MAX_LUX);
        final EnumMap<LuxType, Float> newMap = new EnumMap<>(fractions);
        final float f = getAmount(luxType);
        newMap.put(luxType, amount);
        return new LuxSpectrum(newMap, sum + (amount - f));
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

    public static LuxSpectrum saturate(LuxSpectrum spectrum, final float max) {
        Preconditions.checkArgument(0 <= max);
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            spectrum = spectrum.with(luxType, Math.min(spectrum.getAmount(luxType), max));
        }
        return spectrum;
    }

    public static LuxSpectrum noisySaturate(LuxSpectrum spectrum, final float lower, final float max, final Random random) {
        Preconditions.checkArgument(0 <= lower);
        Preconditions.checkArgument(lower <= max);
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            final float currentMax = MathHelper.lerp(random.nextFloat(), lower, max);
            spectrum = spectrum.with(luxType, Math.min(spectrum.getAmount(luxType), currentMax));
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

    public static Collection<Pair<LuxSpectrum, LuxType>> noisySplit(final LuxSpectrum spectrum, final float lowerNoiseThreshold, final float upperNoiseThreshold, final Random random) {
        Preconditions.checkArgument(0 <= lowerNoiseThreshold);
        Preconditions.checkArgument(lowerNoiseThreshold <= upperNoiseThreshold);
        final LuxSpectrum noise = noisySaturate(spectrum, lowerNoiseThreshold, upperNoiseThreshold, random);
        final LuxSpectrum noiseFree = subtract(spectrum, noise);
        int count = 0;
        LuxType max = null;
        float maxVal = -Float.MAX_VALUE;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            final float amount = noiseFree.getAmount(luxType);
            if (!MathHelper.approximatelyEquals(amount, 0)) {
                count++;
            }
            if (amount > maxVal) {
                max = luxType;
                maxVal = amount;
            }
        }
        if (count == 0) {
            final Collection<Pair<LuxSpectrum, LuxType>> splits = new ObjectArrayList<>(1);
            splits.add(new Pair<>(spectrum, max));
            return splits;
        } else {
            final LuxSpectrum scaledNoise = scale(noise, 1f / count);
            final Collection<Pair<LuxSpectrum, LuxType>> splits = new ObjectArrayList<>(count);
            for (final LuxType luxType : LuxType.LUX_TYPES) {
                final float amount = noiseFree.getAmount(luxType);
                if (!MathHelper.approximatelyEquals(amount, 0)) {
                    splits.add(new Pair<>(noise.with(luxType, amount + scaledNoise.getAmount(luxType)), luxType));
                }
            }
            return splits;
        }
    }

    static {
        EMPTY_SPECTRUM = new LuxSpectrum(new EnumMap<>(LuxType.class), 0);
        LuxSpectrum spectrum = EMPTY_SPECTRUM;
        for (final LuxType luxType : LuxType.LUX_TYPES) {
            spectrum = spectrum.with(luxType, 1);
        }
        WHITE_SPECTRUM = spectrum;
    }
}
