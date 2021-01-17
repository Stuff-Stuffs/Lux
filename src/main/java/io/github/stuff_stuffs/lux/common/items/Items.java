package io.github.stuff_stuffs.lux.common.items;

import io.github.stuff_stuffs.lux.common.blocks.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Items {
    public static final BlockItem EMITTER_BLOCK_ITEM = register(new BlockItem(Blocks.EMITTER_BLOCK, new Item.Settings()), "emitter_block");
    public static final BlockItem MIRROR_BLOCK_ITEM = register(new BlockItem(Blocks.MIRROR_BLOCK, new Item.Settings()), "mirror_block");
    public static final BlockItem HALF_SILVERED_MIRROR_ITEM = register(new BlockItem(Blocks.HALF_SILVERED_MIRROR_BLOCK, new Item.Settings()), "half_silvered_mirror");
    public static final BlockItem BRIGHTNESS_MULTIPLIER_BLOCK_ITEM = register(new BlockItem(Blocks.BRIGHTNESS_MULTIPLIER_BLOCK, new Item.Settings()), "brightness_multiplier_block");
    public static final BlockItem ONE_WAY_MIRROR_BLOCK_ITEM = register(new BlockItem(Blocks.ONE_WAY_MIRROR_BLOCK, new Item.Settings()), "one_way_mirror_block");

    private static <T extends Item> T register(final T item, final String name) {
        return Registry.register(Registry.ITEM, new Identifier("lux", name), item);
    }

    public static void init() {
    }
}
