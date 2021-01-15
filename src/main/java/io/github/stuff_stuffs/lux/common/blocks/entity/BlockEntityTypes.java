package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.LuxApi;
import io.github.stuff_stuffs.lux.common.blocks.Blocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class BlockEntityTypes {
    public static final BlockEntityType<EmitterBlockEntity> EMITTER_BLOCK_ENTITY_TYPE = register(FabricBlockEntityTypeBuilder.create(EmitterBlockEntity::new, Blocks.EMITTER_BLOCK), "emitter");
    public static final BlockEntityType<MirrorBlockEntity> MIRROR_BLOCK_ENTITY_TYPE = register(FabricBlockEntityTypeBuilder.create(MirrorBlockEntity::new, Blocks.MIRROR_BLOCK), "mirror");
    public static final BlockEntityType<HalfSilveredMirrorBlockEntity> HALF_SILVERED_MIRROR_BLOCK_ENTITY_TYPE = register(FabricBlockEntityTypeBuilder.create(HalfSilveredMirrorBlockEntity::new, Blocks.HALF_SILVERED_MIRROR_BLOCK), "half_silvered_mirror");

    private static <T extends BlockEntity> BlockEntityType<T> register(final FabricBlockEntityTypeBuilder<T> builder, final String name) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("lux", name), builder.build());
    }

    public static void init() {
        LuxApi.LUX_RECEIVER_BLOCK_LOOKUP.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof AbstractPaneBlockEntity) {
                return ((AbstractPaneBlockEntity) blockEntity).getLuxReceiver();
            }
            return null;
        }, MIRROR_BLOCK_ENTITY_TYPE, HALF_SILVERED_MIRROR_BLOCK_ENTITY_TYPE);
    }

    private BlockEntityTypes() {
    }
}
