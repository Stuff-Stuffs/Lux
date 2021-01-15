package io.github.stuff_stuffs.lux.client;

import io.github.stuff_stuffs.lux.client.render.LuxBeamRenderer;
import io.github.stuff_stuffs.lux.client.render.MirrorBlockModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LuxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LuxBeamRenderer.init();
        final Identifier identifier = new Identifier("lux", "block/mirror");
        final Identifier identifier1 = new Identifier("lux", "block/half_silvered_mirror");
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(identifier) || resourceId.equals(identifier1)) {
                return new MirrorBlockModel();
            }
            return null;
        });
    }
}
