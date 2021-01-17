package io.github.stuff_stuffs.lux.client.render.entity;

import io.github.stuff_stuffs.lux.common.entity.LuxOrbEntity;
import io.github.stuff_stuffs.lux.common.util.RGBColour;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class LuxOrbEntityRenderer extends EntityRenderer<LuxOrbEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/experience_orb.png");
    private static final RenderLayer LAYER;

    public LuxOrbEntityRenderer(final EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(final LuxOrbEntity entity, final float yaw, final float tickDelta, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(dispatcher.getRotation());
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        final float u1 = 0.25f;
        final float u2 = 0.5f;
        final float v1 = 0;
        final float v2 = 0.25f;
        final Matrix4f model = matrices.peek().getModel();
        final Matrix3f normal = matrices.peek().getNormal();
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        final RGBColour colour = entity.getSpectrum().toColour();
        final RGBColour prevColour = entity.getPrevSpectrum().toColour();
        final RGBColour currentColour = RGBColour.lerp(prevColour, colour, tickDelta);
        final int r = currentColour.getR();
        final int g = currentColour.getG();
        final int b = currentColour.getB();
        final int a = currentColour.getA();
        vertex(vertexConsumer, model, normal, -0.5F, -0.5F, r, g, b, a, u1, v2, light);
        vertex(vertexConsumer, model, normal, 0.5F, -0.5F, r, g, b, a, u2, v2, light);
        vertex(vertexConsumer, model, normal, 0.5F, 0.5F, r, g, b, a, u2, v1, light);
        vertex(vertexConsumer, model, normal, -0.5F, 0.5F, r, g, b, a, u1, v1, light);
        matrices.pop();
    }

    private static void vertex(final VertexConsumer vertexConsumer, final Matrix4f model, final Matrix3f normal, final float x, final float y, final int r, final int g, final int b, final int a, final float u, final float v, final int light) {
        vertexConsumer.vertex(model, x, y, 0.0F).color(r, g, b, a).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normal, 0.0F, 1.0F, 0.0F).next();
    }

    @Override
    public Identifier getTexture(final LuxOrbEntity entity) {
        return TEXTURE;
    }

    static {
        LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);
    }
}
