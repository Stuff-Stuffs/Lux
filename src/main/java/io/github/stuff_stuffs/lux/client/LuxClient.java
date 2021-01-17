package io.github.stuff_stuffs.lux.client;

import io.github.stuff_stuffs.lux.client.network.SpawnLuxReceiver;
import io.github.stuff_stuffs.lux.client.render.MirrorBlockModel;
import io.github.stuff_stuffs.lux.client.render.entity.LuxOrbEntityRenderer;
import io.github.stuff_stuffs.lux.common.entity.EntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LuxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(EntityTypes.LUX_ORB_ENTITY_TYPE, LuxOrbEntityRenderer::new);
        SpawnLuxReceiver.init();
        final Identifier identifier = new Identifier("lux", "block/mirror");
        final Identifier identifier1 = new Identifier("lux", "block/half_silvered_mirror");
        final Identifier identifier2 = new Identifier("lux", "block/one_way_mirror");
        MirrorBlockModel model = new MirrorBlockModel();
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(identifier) || resourceId.equals(identifier1) || resourceId.equals(identifier2)) {
                return model;
            }
            return null;
        });
    }
}
