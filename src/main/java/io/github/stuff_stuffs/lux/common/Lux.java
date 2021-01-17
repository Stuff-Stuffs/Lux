package io.github.stuff_stuffs.lux.common;

import io.github.stuff_stuffs.lux.common.blocks.Blocks;
import io.github.stuff_stuffs.lux.common.entity.EntityTypes;
import io.github.stuff_stuffs.lux.common.items.Items;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Lux implements ModInitializer {
    public static final String MOD_ID = "lux";

    @Override
    public void onInitialize() {
        Blocks.init();
        Items.init();
        EntityTypes.init();
    }

    public static Identifier createId(String path) {
        return new Identifier(MOD_ID, path);
    }
}
