package io.github.stuff_stuffs.lux.common.blocks;

import io.github.stuff_stuffs.lux.common.blocks.entity.BlockEntityTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Blocks {
    public static final EmitterBlock EMITTER_BLOCK = register(new EmitterBlock(AbstractBlock.Settings.of(Material.GLASS)), "emitter");
    public static final MirrorBlock MIRROR_BLOCK = register(new MirrorBlock(AbstractBlock.Settings.of(Material.GLASS)), "mirror");
    public static final HalfSilveredMirrorBlock HALF_SILVERED_MIRROR_BLOCK = register(new HalfSilveredMirrorBlock(), "half_silvered_mirror");

    private static <T extends Block> T register(final T block, final String name) {
        return Registry.register(Registry.BLOCK, new Identifier("lux", name), block);
    }

    public static void init() {
        BlockEntityTypes.init();
    }

    private Blocks() {
    }
}
