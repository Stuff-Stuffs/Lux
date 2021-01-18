package io.github.stuff_stuffs.lux.common.blocks;

import io.github.stuff_stuffs.lux.common.blocks.entity.AbstractAxisBlockEntity;
import io.github.stuff_stuffs.lux.common.blocks.entity.PrismBlockEntity;
import io.github.stuff_stuffs.lux.common.entity.EntityTypes;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PrismBlock extends Block implements BlockEntityProvider {
    public PrismBlock() {
        this(Settings.of(Material.GLASS));
    }

    public PrismBlock(final Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return new PrismBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (!world.isClient()) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractAxisBlockEntity) {
                final BlockPos blockPos = player.getBlockPos().subtract(pos);
                final Vec3d delta = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).normalize().multiply(-1);
                ((AbstractAxisBlockEntity) blockEntity).setAxis(delta);
            }
        }
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCullingShape(final BlockState state, final BlockView world, final BlockPos pos) {
        return VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context) {
        return VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context) {
        if (context instanceof EntityShapeContext) {
            return ((EntityShapeContext) context).getEntity().filter(entity -> entity.getType() == EntityTypes.LUX_ORB_ENTITY_TYPE).isPresent() ? super.getCollisionShape(state, world, pos, context) : VoxelShapes.empty();
        }
        return VoxelShapes.empty();
    }
}
