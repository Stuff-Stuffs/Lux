package io.github.stuff_stuffs.lux.common.blocks;

import io.github.stuff_stuffs.lux.common.blocks.entity.BlockEntityTypes;
import io.github.stuff_stuffs.lux.common.blocks.entity.BrightnessMultiplierBlockEntity;
import io.github.stuff_stuffs.lux.common.blocks.entity.HalfSilveredMirrorBlockEntity;
import io.github.stuff_stuffs.lux.common.blocks.entity.MirrorBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Blocks {
    public static final EmitterBlock EMITTER_BLOCK = register(new EmitterBlock(AbstractBlock.Settings.of(Material.GLASS)), "emitter");
    public static final Block MIRROR_BLOCK = register(new PlaneBlock(MirrorBlockEntity::new), "mirror");
    public static final Block HALF_SILVERED_MIRROR_BLOCK = register(new PlaneBlock(HalfSilveredMirrorBlockEntity::new), "half_silvered_mirror");
    public static final Block BRIGHTNESS_MULTIPLIER_BLOCK = register(new PlaneBlock(BrightnessMultiplierBlockEntity::new), "brightness_multiplier");

    private static <T extends Block> T register(final T block, final String name) {
        return Registry.register(Registry.BLOCK, new Identifier("lux", name), block);
    }

    public static void init() {
        BlockEntityTypes.init();
    }

    private Blocks() {
    }
}
