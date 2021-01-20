package io.github.stuff_stuffs.lux.common.items;

import io.github.stuff_stuffs.lux.common.blocks.Blocks;
import io.github.stuff_stuffs.lux.common.blocks.entity.ThresholdingMirrorBlockEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThresholdingMirrorBlockItem extends BlockItem {
    public ThresholdingMirrorBlockItem(final Settings settings) {
        super(Blocks.THRESHOLDING_MIRROR_BLOCK, settings);
    }


    @Override
    public ActionResult place(final ItemPlacementContext context) {
        final ActionResult ret = super.place(context);
        final World world = context.getWorld();
        if (ret.isAccepted() && !world.isClient() && context.getPlayer() != null) {
            final BlockPos blockPos = context.getBlockPos();
            final BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof ThresholdingMirrorBlockEntity) {
                final CompoundTag tag = context.getPlayer().getStackInHand(context.getHand()).getOrCreateTag();
                final float threshold = tag.contains("threshold", NbtType.FLOAT) ? tag.getFloat("threshold") : 100;
                ((ThresholdingMirrorBlockEntity) blockEntity).setThreshold(threshold);
            }
        }
        return ret;
    }
}
