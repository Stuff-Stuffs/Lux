package io.github.stuff_stuffs.lux.common.lux;

import io.github.stuff_stuffs.lux.common.util.HSVColour;
import io.github.stuff_stuffs.lux.common.util.RGBColour;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Random;

public enum LuxType {
    //TODO more types
    RED(get(0, 4)),
    GREEN(get(1, 4)),
    BLUE(get(2, 4)),
    PURPLE(get(3, 4));
    public static final Iterable<LuxType> LUX_TYPES = new Iterable<LuxType>() {
        private final LuxType[] luxTypes = LuxType.values();

        @NotNull
        @Override
        public Iterator<LuxType> iterator() {
            return new ObjectArrayIterator<>(luxTypes);
        }
    };
    public static final int LUX_TYPE_COUNT = LuxType.values().length;
    private static final LuxType[] LUX_TYPE_ARR = values();
    private final RGBColour colour;

    LuxType(final RGBColour colour) {
        this.colour = colour;
    }

    public RGBColour getColour() {
        return colour;
    }

    public int getId() {
        return ordinal();
    }

    public static LuxType byId(final int id) {
        return LUX_TYPE_ARR[id];
    }

    public static LuxType getRandom(final Random random) {
        return LUX_TYPE_ARR[random.nextInt(LUX_TYPE_COUNT)];
    }

    private static RGBColour get(final int index, final int count) {
        final double prog = index / (double) count * 360d;
        return new HSVColour(prog, 1, 1).toRgb(255);
    }
}
