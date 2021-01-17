package io.github.stuff_stuffs.lux.common.network;

import io.github.stuff_stuffs.lux.common.Lux;
import io.github.stuff_stuffs.lux.common.entity.LuxOrbEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class LuxOrbSpawnS2C {
    public static final Identifier IDENTIFIER = Lux.createId("spawn_orb_s2c");
    public static Packet<?> create(LuxOrbEntity entity) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(entity.getId());
        buf.writeUuid(entity.getUuid());
        buf.writeDouble(entity.getPos().x);
        buf.writeDouble(entity.getPos().y);
        buf.writeDouble(entity.getPos().z);
        buf.writeFloat((float) entity.getVelocity().y);
        buf.writeFloat((float) entity.getVelocity().z);
        buf.writeFloat((float) entity.getVelocity().x);
        buf.writeFloat(entity.pitch);
        buf.writeFloat(entity.yaw);
        buf.writeFloat(entity.getFocus());
        entity.getSpectrum().toBuf(buf);
        return ServerPlayNetworking.createS2CPacket(IDENTIFIER, buf);
    }
}
