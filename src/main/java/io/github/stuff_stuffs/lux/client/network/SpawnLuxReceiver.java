package io.github.stuff_stuffs.lux.client.network;

import io.github.stuff_stuffs.lux.common.entity.EntityTypes;
import io.github.stuff_stuffs.lux.common.entity.LuxOrbEntity;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import io.github.stuff_stuffs.lux.common.network.LuxOrbSpawnS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class SpawnLuxReceiver {
    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler handler, final PacketByteBuf buf, final PacketSender responseSender) {
        final int id = buf.readVarInt();
        final UUID uuid = buf.readUuid();
        final double x = buf.readDouble();
        final double y = buf.readDouble();
        final double z = buf.readDouble();
        final double dx = buf.readFloat();
        final double dy = buf.readFloat();
        final double dz = buf.readFloat();
        final float pitch = buf.readFloat();
        final float yaw = buf.readFloat();
        float focus = buf.readFloat();
        final LuxSpectrum luxPacket = LuxSpectrum.fromBuf(buf);
        client.execute(() -> {
            final ClientWorld world = client.world;
            final LuxOrbEntity luxEntity = new LuxOrbEntity(world, new Vec3d(x, y, z), new Vec3d(dx, dy, dz), luxPacket, focus);
            luxEntity.setEntityId(id);
            luxEntity.setUuid(uuid);
            luxEntity.pitch = pitch;
            luxEntity.setYaw(yaw);
            luxEntity.updateTrackedPosition(new Vec3d(x,y,z));
            world.addEntity(id, luxEntity);
        });
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(LuxOrbSpawnS2C.IDENTIFIER, SpawnLuxReceiver::receive);
    }
}
