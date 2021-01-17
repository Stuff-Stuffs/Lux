package io.github.stuff_stuffs.lux.common.api;

import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface LuxReceiver {
    @Nullable
    Vec3d receive(LuxOrb luxOrb, final BlockPos blockPos, final World world);
}
