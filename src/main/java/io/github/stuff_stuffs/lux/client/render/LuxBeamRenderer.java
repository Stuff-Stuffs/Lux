package io.github.stuff_stuffs.lux.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import io.github.stuff_stuffs.lux.common.util.LuxBeamUtil;
import io.github.stuff_stuffs.lux.common.util.RGBColour;
import io.github.stuff_stuffs.lux.common.util.VecUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.List;

public class LuxBeamRenderer {
    private static final List<LuxBeam.RenderableBeam> RENDER_QUEUE = new ObjectArrayList<>(32);
    private static Sprite SPRITE;

    public static void enqueueRender(final LuxBeam.RenderableBeam beam) {
        RENDER_QUEUE.add(beam);
    }

    private static void render(final WorldRenderContext context) {
        if (RENDER_QUEUE.size() == 0) {
            return;
        }
        if (SPRITE == null) {
            SPRITE = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("minecraft", "block/white_wool"));
        }
        final Frustum frustum = context.frustum();
        assert frustum != null;
        final Camera camera = context.camera();
        final double cx = camera.getPos().x;
        final double cy = camera.getPos().y;
        final double cz = camera.getPos().z;
        final VertexConsumerProvider vertices = context.consumers();
        assert vertices != null;
        final VertexConsumer vertexConsumer = vertices.getBuffer(RenderLayer.getTranslucent());
        final MatrixStack matrices = context.matrixStack();
        final float tickDelta = context.tickDelta();
        for (final LuxBeam.RenderableBeam beam : RENDER_QUEUE) {
            final Box box = LuxBeamUtil.boxFromBeam(beam);
            if (frustum.isVisible(box)) {
                matrices.push();
                final Vec3d interpolated = VecUtil.lerp(beam.getPrevPos(), beam.getPos(), tickDelta);
                matrices.translate(interpolated.x - cx, interpolated.y - cy, interpolated.z - cz);
                renderBeam(beam, interpolated, camera, matrices, vertexConsumer, tickDelta);
                matrices.pop();
            }
        }
    }

    private static void renderBeam(final LuxBeam.RenderableBeam beam, final Vec3d interpBeamPos, final Camera camera, final MatrixStack matrices, final VertexConsumer vertexConsumer, final float tickDelta) {
        final Vec3d interpolatedDirection = VecUtil.slerp(beam.getPrevDirection(), beam.getDirection(), tickDelta);
        final double pitch = Math.asin(-interpolatedDirection.y);
        final double yaw = MathHelper.atan2(interpolatedDirection.x, interpolatedDirection.z);
        if (yaw != 0.0) {
            final Quaternion quaternion = Vec3f.POSITIVE_Y.getRadialQuaternion((float) yaw);
            matrices.multiply(quaternion);
        }

        if (pitch != 0.0) {
            final Quaternion quaternion = Vec3f.POSITIVE_X.getRadialQuaternion((float) pitch);
            matrices.multiply(quaternion);
        }
        matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((float) getAngle(interpolatedDirection, interpBeamPos, camera.getPos())));
        final double length = beam.getLength();
        final Matrix4f model = matrices.peek().getModel();
        final Matrix3f normal = matrices.peek().getNormal();
        final RGBColour startColour = beam.getSpectrum().toColour();
        final RGBColour endColour = beam.getRemainingSpectrum(beam.getLength()).toColour();
        renderBeamFace(model, normal, vertexConsumer, new RGBColour(0, 0, 255), endColour, length, -0.125f, -0.125f, -0.125f, 0.125f, SPRITE.getMinU(), SPRITE.getMaxU(), SPRITE.getMaxV(), SPRITE.getMinV());
        renderBeamFace(model, normal, vertexConsumer, new RGBColour(0, 255, 0), endColour, length, 0.125f, 0.125f, 0.125f, -0.125f, SPRITE.getMaxU(), SPRITE.getMinU(), SPRITE.getMinV(), SPRITE.getMaxV());

        renderBeamFace(model, normal, vertexConsumer, startColour, endColour, length, -0.125f, 0.125f, 0.125f, 0.125f, SPRITE.getMinU(), SPRITE.getMaxU(), SPRITE.getMaxV(), SPRITE.getMinV());
        renderBeamFace(model, normal, vertexConsumer, new RGBColour(255, 0, 0), endColour, length, 0.125f, -0.125f, -0.125f, -0.125f, SPRITE.getMaxU(), SPRITE.getMinU(), SPRITE.getMinV(), SPRITE.getMaxV());
    }

    private static void renderBeamFace(final Matrix4f modelMatrix, final Matrix3f normalMatrix, final VertexConsumer vertices, final RGBColour first, final RGBColour second, final double length, final float x1, final float z1, final float x2, final float z2, final float u1, final float u2, final float v1, final float v2) {
        vertex(x1, z1, 0, first.getR(), first.getG(), first.getB(), first.getA(), u2, v1, vertices, modelMatrix, normalMatrix);
        vertex(x1, z1, length, second.getR(), second.getG(), second.getB(), second.getA(), u2, v2, vertices, modelMatrix, normalMatrix);
        vertex(x2, z2, length, second.getR(), second.getG(), second.getB(), second.getA(), u1, v2, vertices, modelMatrix, normalMatrix);
        vertex(x2, z2, 0, first.getR(), first.getG(), first.getB(), first.getA(), u1, v1, vertices, modelMatrix, normalMatrix);
    }

    private static void vertex(final double x, final double y, final double z, final int r, final int g, final int b, final int a, final float u, final float v, final VertexConsumer consumer, final Matrix4f model, final Matrix3f normal) {
        consumer.vertex(model, (float) x, (float) y, (float) z).color(r, g, b, a).texture(u, v).light(15728880).normal(normal, 0, 1, 0).next();
    }

    private static double getAngle(final Vec3d direction, final Vec3d origin, final Vec3d point) {
        final Vec3d p = VecUtil.projectOntoPlane(direction, origin, point).subtract(origin);
        final Vec3d axis = VecUtil.cross(direction, new Vec3d(0, point.z < 0 ? 1 : 1, 0));
        double dot = p.dotProduct(axis) / (p.length());
        final Vec3d cross = VecUtil.cross(p, origin);
        boolean reverse = true;
        if (direction.dotProduct(cross) > 0) { // Or > 0
            reverse = false;
        }
        dot = MathHelper.clamp(dot, -1, 1);
        final double angle = Math.acos(dot);
        return reverse ? -angle : angle;
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(LuxBeamRenderer::render);
        ClientTickEvents.START_CLIENT_TICK.register(client -> RENDER_QUEUE.clear());
        InvalidateRenderStateCallback.EVENT.register(() -> {
            SPRITE = null;
            RenderSystem.texParameter(3553, 10242, 10497);
            RenderSystem.texParameter(3553, 10243, 10497);
        });
    }
}
