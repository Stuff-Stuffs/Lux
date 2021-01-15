package io.github.stuff_stuffs.lux.common.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public final class Util {
    private static final boolean CLIENT_ENV = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    public static boolean isOnClientThread() {
        return CLIENT_ENV && isOnClientThread0();
    }

    private static boolean isOnClientThread0() {
        return MinecraftClient.getInstance().isOnThread();
    }

    private Util() {

    }
}
