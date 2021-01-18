package io.github.stuff_stuffs.lux.common.blocks;

import io.github.stuff_stuffs.lux.common.blocks.entity.AbstractPlaneBlockEntity;
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

import java.util.function.BiFunction;

public class PlaneBlock extends Block implements BlockEntityProvider {
    private final BiFunction<BlockPos, BlockState, ? extends AbstractPlaneBlockEntity> blockEntityFactory;

    public PlaneBlock(final BiFunction<BlockPos, BlockState, ? extends AbstractPlaneBlockEntity> blockEntityFactory) {
        this(Settings.of(Material.GLASS), blockEntityFactory);
    }

    public PlaneBlock(final Settings settings, final BiFunction<BlockPos, BlockState, ? extends AbstractPlaneBlockEntity> blockEntityFactory) {
        super(settings);
        this.blockEntityFactory = blockEntityFactory;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (!world.isClient()) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractPlaneBlockEntity) {
                final BlockPos blockPos = player.getBlockPos().subtract(pos);
                final Vec3d delta = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).normalize().multiply(-1);
                ((AbstractPlaneBlockEntity) blockEntity).setPlaneNormal(delta);
            }
        }
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context) {
        if (context instanceof EntityShapeContext) {
            return ((EntityShapeContext) context).getEntity().filter(entity -> entity.getType()== EntityTypes.LUX_ORB_ENTITY_TYPE).isPresent()?VoxelShapes.empty():super.getCollisionShape(state, world, pos, context);
        }
        return VoxelShapes.empty();
    }
}
