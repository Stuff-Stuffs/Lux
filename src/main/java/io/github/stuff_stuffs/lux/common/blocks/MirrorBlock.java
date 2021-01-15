package io.github.stuff_stuffs.lux.common.blocks;

import io.github.stuff_stuffs.lux.common.blocks.entity.BlockEntityTypes;
import io.github.stuff_stuffs.lux.common.blocks.entity.MirrorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MirrorBlock extends Block implements BlockEntityProvider {
    public MirrorBlock(final Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return new MirrorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final World world, final BlockState state, final BlockEntityType<T> type) {
        if (type == BlockEntityTypes.MIRROR_BLOCK_ENTITY_TYPE) {
            return (world1, pos, state1, blockEntity) -> ((MirrorBlockEntity) blockEntity).tick();
        }
        return null;
    }

    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (!world.isClient()) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MirrorBlockEntity) {
                BlockPos blockPos = player.getBlockPos().subtract(pos);
                final Vec3d delta = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).normalize().multiply(-1);
                ((MirrorBlockEntity) blockEntity).setPlaneNormal(delta);
            }
        }
        return ActionResult.SUCCESS;
    }
}
