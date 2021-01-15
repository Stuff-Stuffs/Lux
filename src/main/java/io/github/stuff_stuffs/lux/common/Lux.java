package io.github.stuff_stuffs.lux.common;

import io.github.stuff_stuffs.lux.common.blocks.Blocks;
import io.github.stuff_stuffs.lux.common.items.Items;
import net.fabricmc.api.ModInitializer;

public class Lux implements ModInitializer {
    @Override
    public void onInitialize() {
        Blocks.init();
        Items.init();
    }
}
