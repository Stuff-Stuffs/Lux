package io.github.stuff_stuffs.lux.client.render;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.lux.common.util.math.VecUtil;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class MirrorBlockModel implements FabricBakedModel, BakedModel, UnbakedModel {
    private static final Identifier CARPET_ID = new Identifier("minecraft", "block/white_carpet");
    private BakedModel carpetModel;

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(final BlockRenderView blockView, final BlockState state, final BlockPos pos, final Supplier<Random> randomSupplier, final RenderContext context) {
        final Vec3f vec3f = new Vec3f();
        Vec3d normal = (Vec3d) ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
        if (normal == null) {
            normal = new Vec3d(1, 0, 0);
        }
        final Quaternion quaternion = VecUtil.directionToQuaternion(normal);
        quaternion.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
        final Matrix3f matrix3f = new Matrix3f(quaternion);
        context.pushTransform(quad -> {
            for (int i = 0; i < 4; i++) {
                quad.copyPos(i, vec3f);
                vec3f.add(0, 15 / 32f, 0);
                vec3f.add(-0.5f, -0.5f, -0.5f);
                vec3f.transform(matrix3f);
                vec3f.add(0.5f, 0.5f, 0.5f);
                quad.pos(i, vec3f);

                if(quad.copyNormal(i, vec3f)!=null) {
                    vec3f.transform(matrix3f);
                    quad.normal(i, vec3f);
                }
            }
            final Direction cullFace = quad.cullFace();
            if (cullFace != null) {
                vec3f.set(cullFace.getOffsetX(), cullFace.getOffsetY(), cullFace.getOffsetZ());
                vec3f.transform(matrix3f);
                Direction transformedCullFace = Direction.getFacing(vec3f.getX(), vec3f.getY(), vec3f.getZ());
                quad.cullFace(transformedCullFace);
            }
            return true;
        });
        final FabricBakedModel fabricBakedModel = (FabricBakedModel) carpetModel;
        if (fabricBakedModel.isVanillaAdapter()) {
            context.fallbackConsumer().accept(carpetModel);
        } else {
            fabricBakedModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        }
        context.popTransform();
    }

    @Override
    public void emitItemQuads(final ItemStack stack, final Supplier<Random> randomSupplier, final RenderContext context) {
        final Vec3f vec3f = new Vec3f();
        final Vec3d normal = Vec3d.ZERO;
        final Matrix3f matrix3f = new Matrix3f(VecUtil.directionToQuaternion(normal));
        context.pushTransform(quad -> {
            for (int i = 0; i < 4; i++) {
                quad.copyPos(i, vec3f);
                vec3f.transform(matrix3f);
                vec3f.add(0.5f, 0.5f, 0.5f);
                quad.pos(i, vec3f);

                quad.copyNormal(i, vec3f);
                vec3f.transform(matrix3f);
                quad.normal(i, vec3f);
            }
            return true;
        });
        final FabricBakedModel fabricBakedModel = (FabricBakedModel) carpetModel;
        if (fabricBakedModel.isVanillaAdapter()) {
            context.fallbackConsumer().accept(carpetModel);
        } else {
            fabricBakedModel.emitItemQuads(stack, randomSupplier, context);
        }
        context.popTransform();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return carpetModel.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.singleton(CARPET_ID);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptySet();
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<SpriteIdentifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer, final Identifier modelId) {
        carpetModel = loader.bake(CARPET_ID, rotationContainer);
        return this;
    }
}
