package io.github.stuff_stuffs.lux.common.api;

import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;
import net.minecraft.util.Identifier;

public final class LuxApi {
    public static final BlockApiLookup<LuxReceiver, Void> LUX_RECEIVER_BLOCK_LOOKUP = BlockApiLookupRegistry.getLookup(new Identifier("lux", "lux_receiver"), LuxReceiver.class, Void.class);

    private LuxApi() {
    }
}
